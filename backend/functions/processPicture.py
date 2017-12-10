import pyrebase
import json
from config.config import config
from checkAuth import user_is_authenticated
import sys
import logging
import os
from PIL import Image
from google.cloud import vision
from google.cloud.vision import types
import http.client, urllib.parse
import tempfile


def main(kwargs_dict):
    logging.basicConfig(filename='logs/server.log',format='%(asctime)s %(message)s', level=logging.DEBUG)
    os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = config["serviceAccount"]

    firebase = pyrebase.initialize_app(config)
    fb_db = firebase.database()
    fb_storage = firebase.storage()

    return process_picture(fb_db, fb_storage, **kwargs_dict)


def process_picture(fb_db, fb_storage, picture_path, userToken):
    authenticated, uid = user_is_authenticated(userToken)
    if authenticated:
        logging.debug("Processing picture: " + picture_path)

        arr = picture_path.split("/")
        if (len(arr) != 4):
            return "picture_path has to be of form "\
                   "pictures/group_id/user_id/picture_name, was: " + picture_path
        picture_group_id = arr[1]
        picture_user_id = arr[2]
        picture_name = arr[3]
        picture_remote_folder = "/".join(arr[:3])

        if (picture_user_id != uid):
            return "Stored picture user must macth request user"

        # no need to verify the group, users only have permissions to their own folders
        # and the user group might have changed since the upload, so always publish to upload group
        # however, the group may be deleted because the creator left the group
        # using the expiry time as an indicator that the group exists

        if (not fb_db.child("groups").child(picture_group_id).child("expiry").get().val()):
            return "Group has been deleted"

        tmp_folder = tempfile.mkdtemp(prefix="mcc-fall-2017-g18")
        picture_local_path = os.path.join(tmp_folder, picture_name)
        fb_storage.child(picture_path).download(picture_local_path)

        im = Image.open(picture_local_path)
        try:
            exif = im._getexif()
            orient = exif[274]
            orient_list = [0, 0, 180, 0, 0, 270, 0, 90]
            degrees = orient_list[orient - 1]
            if degrees:
                im = im.rotate(degrees, expand=True)
        except Exception:
            pass
        # assume landscape orientation
        larger_dim, smaller_dim = im.size
        is_portrait = False
        if (smaller_dim > larger_dim):
            tmp = larger_dim
            larger_dim = smaller_dim
            smaller_dim = tmp
            is_portrait = True

        downscale_params = [{"width": 1280, "height": 960, "filename": picture_name},
                            {"width":  640, "height": 480, "filename": picture_name}]
        face_detection_picture_path = None
        for p in downscale_params:
            if (larger_dim <= p["width"] and smaller_dim <= p["height"]):
                continue
            p["filename"] += "_" + str(p["width"])
            if (is_portrait):
                dims = (p["height"], p["width"])
            else:
                dims = (p["width"], p["height"])
            im.thumbnail(dims, Image.ANTIALIAS)
            p_local_path = os.path.join(tmp_folder, p["filename"])
            with open(p_local_path, "a+b") as p_file:
                p_file.truncate()
                im.save(p_file, "JPEG")
                p_file.seek(0)
                fb_storage.child(picture_remote_folder + "/" + p["filename"]).put(p_file)
            if not face_detection_picture_path:
                face_detection_picture_path = p_local_path
            else:
                os.unlink(p_local_path)
        im.close()
        if not face_detection_picture_path:
            # if no downscaled pictures, use the original for face detection
            face_detection_picture_path = picture_local_path
        else:
            # otherwise the original is no longer needed
            os.unlink(picture_local_path)

        an_client = vision.ImageAnnotatorClient()
        an_file = open(face_detection_picture_path, "rb")
        an_content = an_file.read()
        an_file.close()
        an_image = types.Image(content=an_content)
        an_annotations = an_client.face_detection(image=an_image).face_annotations
        contains_people = False
        if (len(an_annotations) > 0):
            contains_people = True

        os.unlink(face_detection_picture_path)
        os.rmdir(tmp_folder)

        picture_data = {
            "user_id": uid,
            "picture": picture_path,
            "contains_people": contains_people
        }
        for p in downscale_params:
            picture_data["picture_" + str(p["width"])] = picture_remote_folder + "/" + p["filename"]

        fb_db.child("groups").child(picture_group_id).child("pictures").push(picture_data)

        oauth_token = fb_db.credentials.get_access_token().access_token
        cloud_msg = {
          "message":{
            "topic" : picture_group_id,
            "notification" : {
              "body" : "new picture body",
              "title" : "new picture title",
              }
           }
        }
        cloud_msg_json = json.dumps(cloud_msg)
        headers = {"Content-type": "application/json",
                   "Accept": "text/plain",
                   "Authorization": "Bearer "+oauth_token}
        conn = http.client.HTTPSConnection("fcm.googleapis.com")
        conn.request("POST", "/v1/projects/mcc-fall-2017-g18/messages:send", cloud_msg_json, headers)
        response = conn.getresponse()
        print(response.status, response.reason)
        data = response.read()
        print(data)
        conn.close()

        logging.debug("Successfully processed picture: " + picture_path)
        return "success"
    else:
        return "User authentication failed"

if __name__ == "__main__":
    print(main(json.loads(sys.argv[1])))
