import pyrebase
import json
from config.config import config
from checkAuth import user_is_authenticated
import sys
import logging
from time import time
import os,binascii
import joinGroup
import firebase_admin
import uuid


def main(kwargs_dict):
    logging.basicConfig(filename='/tmp/server.log',format='%(asctime)s %(message)s', level=logging.DEBUG)
    firebase = pyrebase.initialize_app(config)
    db = firebase.database()
    return create_group(db, **kwargs_dict)

def create_group(db, groupname, timeToLive, userToken):
    authenticated, uid = user_is_authenticated(userToken)
    if authenticated:
        if joinGroup.isInGroup(db, uid):
            return "Can't create group, already in group"
        username = getUsername(db, uid)
        logging.debug("Creating group data")
        userData = {
            "name":username,
            "QR":"notready"
        }
        members = {
            uid:userData
        }
        expiry = get_expiry(int(timeToLive))
        data = {
            "owner": username,
            "ownerID": uid,
            "name": groupname,
            "expiry": expiry,
            "members":members
        }
        logging.debug("Pushing group to database")
        result = db.child("groups").push(data)

        groupID = result["name"]

        # Updating the proper QR code to the group
        QR = createQR(groupID,uid)
        db.child("groups").child(groupID).child("members").child(uid).update({"QR":QR})

        db.child("users").child(uid).update({"groupID":groupID})

        # set group id and expiry time in user token for Firebase Storage rules
        setUserTokenParams(uid, groupID, expiry)

        returnData = {
            "groupID":groupID,
            "QRtoken":QR
        }
        logging.debug("Returning group data")
        return json.dumps(returnData)
    else:
        return "User authentication failed"

def getUsername(db, userID):
    name = db.child("users").child(userID).child("userName").get().val()
    return str(name)

#defines the expiry time in Unix Epoch Time
def get_expiry(timeToLive):
    return time()+60*timeToLive

#Creates a QR token with groupID, userID and random string
def createQR(groupID, usrID):
    random = random = binascii.b2a_hex(os.urandom(15)).decode("utf-8")
    return groupID+'/'+usrID+'/'+random

def setUserTokenParams(uid, groupID, expiry):
    # TODO: firebase_admin is initialized too many times already
    # please initialize only once in some appropriate place
    # TODO: move this part to some common place
    cred = firebase_admin.credentials.Certificate(config["serviceAccount"])
    admin_app = firebase_admin.initialize_app(cred, name=str(uuid.uuid4()))
    custom_claims = {
        'groupID': groupID,
        'groupExpiry': expiry
    }
    try:
        firebase_admin.auth.set_custom_user_claims(uid, custom_claims, app=admin_app)
    except:
        # better to crash the app than try to hide the errors
        raise
    finally:
        firebase_admin.delete_app(admin_app)

if __name__ == "__main__":
    print(main(json.loads(sys.argv[1])))
