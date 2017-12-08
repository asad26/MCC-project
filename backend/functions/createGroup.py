import pyrebase
import json
from config.config import config
from checkAuth import user_is_authenticated
import sys
import logging
from time import time
import os,binascii
import joinGroup

def main(kwargs_dict):
    logging.basicConfig(filename='logs/server.log',format='%(asctime)s %(message)s', level=logging.DEBUG)
    firebase = pyrebase.initialize_app(config)
    db = firebase.database()
    return create_group(db, **kwargs_dict)

def create_group(db, groupname, username, timeToLive, userToken):
    authenticated, uid = user_is_authenticated(userToken)
    if authenticated:
        if joinGroup.isInGroup(db, uid):
            return "Can't create group, already in group"
        logging.debug("Creating group data")
        userData = {
            "name":username,
            "QR":"notready"
        }
        members = {
            uid:userData
        }
        data = {
            "owner": username,
            "ownerID": uid,
            "name": groupname,
            "expiry":get_expiry(int(timeToLive)),
            "members":members
        }
        logging.debug("Pushing group to database")
        result = db.child("groups").push(data)

        groupID = result["name"]

        # Updating the proper QR code to the group
        QR = createQR(groupID,uid)
        db.child("groups").child(groupID).child("members").child(uid).update({"QR":QR})

        db.child("users").child(uid).update({"groupID":groupID})

        returnData = {
            "groupID":groupID,
            "QRtoken":QR
        }
        logging.debug("Returning group data")
        return json.dumps(returnData)
    else:
        return "User authentication failed"

#defines the expiry time in Unix Epoch Time
def get_expiry(timeToLive):
    return time()+60*timeToLive

#Creates a QR token with groupID, userID and random string
def createQR(groupID, usrID):
    random = random = binascii.b2a_hex(os.urandom(15)).decode("utf-8")
    return groupID+'/'+usrID+'/'+random

if __name__ == "__main__":
    print(main(json.loads(sys.argv[1])))
