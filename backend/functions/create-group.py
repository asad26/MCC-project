import pyrebase
import json
from config.config import config
from checkAuth import user_is_authenticated
import sys
import logging
from time import time
import os,binascii

def main():
    logging.basicConfig(filename='logs/server.log',format='%(asctime)s %(message)s', level=logging.DEBUG)
    firebase = pyrebase.initialize_app(config)
    db = firebase.database()
    arguments=json.loads(sys.argv[1])
    create_group(db,arguments["groupname"],arguments["username"],arguments["timeToLive"],arguments["userToken"])

def create_group(db,groupname, user, timeToLive, usertoken):
    authenticated, uid = user_is_authenticated(usertoken)
    if authenticated:
        logging.debug("Creating group data")
        userData = {
            "name":user,
            "QR":"notready"
        }
        members = {
            uid:userData
        }
        data = {
            "owner": user,
            "ownerID": uid,
            "name": groupname,
            "expiry":get_expiry(timeToLive),
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
        print(json.dumps(returnData),end='')
    else:
        print("User authentication failed")

#defines the expiry time in Unix Epoch Time
def get_expiry(timeToLive):
    return time()+60*timeToLive

#Creates a QR token with groupID, userID and random string
def createQR(groupID, usrID):
    random = random = binascii.b2a_hex(os.urandom(15)).decode("utf-8")
    return groupID+'/'+usrID+'/'+random

if __name__ == "__main__":
    main()
