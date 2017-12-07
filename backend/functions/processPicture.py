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

        ind = picture_path.find("/")
        picture_group = picture_path[:ind]
        picture_name = picture_path[ind + 1:]

        tmp_folder = "tmp"
        picture_local_path = os.path.join(tmp_folder, picture_name)
        fb_storage.child(picture_path).download(picture_local_path)

        im = Image.open(picture_local_path)
        width, height = im.size

        downscale_params = [{"width": 1280, "height": 960, "filename": picture_name},
                            {"width":  640, "height": 480, "filename": picture_name}]
        face_detection_picture_path = None
        for p in downscale_params:
            if (width > p["width"] or height > p["height"]):
                p["filename"] += "_" + str(p["width"])
                im.thumbnail((p["width"], p["height"]), Image.ANTIALIAS)
                p_local_path = os.path.join(tmp_folder, p["filename"])
                im.save(p_local_path, "JPEG")
                fb_storage.child(picture_group + "/" + p["filename"]).put(p_local_path)
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

        picture_data = {
            "user_id": uid,
            "picture": picture_path,
            "contains_people": contains_people
        }
        for p in downscale_params:
            picture_data["picture_" + str(p["width"])] = picture_group + "/" + p["filename"]

        fb_db.child("groups").child(picture_group).child("pictures").push(picture_data)

        oauth_token = fb_db.credentials.get_access_token().access_token
        cloud_msg = {
          "message":{
            "topic" : picture_group,
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
