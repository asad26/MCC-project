import pyrebase
import json
from config.config import config
from checkAuth import user_is_authenticated
import sys
import logging
from deleteExpired import deleteGroup
from joinGroup import isInGroup

def main(kwargs_dict):
    logging.basicConfig(filename='logs/server.log',format='%(asctime)s %(message)s', level=logging.DEBUG)
    logging.debug("Running leave or delete group")
    firebase = pyrebase.initialize_app(config)
    db = firebase.database()
    usertoken = kwargs_dict["userToken"]
    authenticated, userID = user_is_authenticated(usertoken)
    fb_storage = firebase.storage()
    if authenticated:
        if not isInGroup(db, userID):
            return "Not in any group"
        logging.debug("Getting group ID")
        groupID = db.child("users").child(userID).child("groupID").get().val()
        logging.debug("Found group: "+str(groupID))
        if isOwner(db, userID, groupID):
            return deleteGroup(db, fb_storage, groupID)
        else:
            return leaveGroup(db,userID, groupID)

def isOwner(db, userID, groupID):
    owner = db.child("groups").child(groupID).child("ownerID").get().val()
    return owner==userID

def leaveGroup(db, userID, groupID):
    logging.debug("Removing group "+groupID+" from "+userID+" table")
    #Remove from user table
    db.child("users").child(userID).child("groupID").remove()
    logging.debug("Removing user "+userID+" from "+groupID+" table")
    #Remove from group table
    db.child("groups").child(groupID).child("members").child(userID).remove()
    return "Removed user "+userID+" from group "+groupID

if __name__ == "__main__":
    print(main(json.loads(sys.argv[1])))
