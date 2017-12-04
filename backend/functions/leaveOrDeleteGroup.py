import pyrebase
import json
from config.config import config
from checkAuth import user_is_authenticated
import sys
import logging
from deleteExpired import deleteGroup

logging.basicConfig(filename='logs/server.log',format='%(asctime)s %(message)s', level=logging.DEBUG)
logging.debug("Running leave or delete group")
firebase = pyrebase.initialize_app(config)
db = firebase.database()

arguments=json.loads(sys.argv[1])

def isOwner(userID, groupID):
    owner = db.child("groups").child(groupID).child("ownerID").get().val()
    return owner==userID

def leaveGroup(userID, groupID):
    logging.debug("Removing group "+groupID+" from "+userID+" table")
    #Remove from user table
    db.child("users").child(userID).child("groupID").remove()
    logging.debug("Removing user "+userID+" from "+groupID+" table")
    #Remove from group table
    db.child("groups").child(groupID).child("members").child("userID").remove()
    print("Removed user "+userID+" from group "+groupID)

usertoken = arguments["userToken"]
authenticated, userID = user_is_authenticated(usertoken)
if authenticated:
    logging.debug("Getting group ID")
    groupID = db.child("users").child(userID).child("groupID").get().val()
    logging.debug("Found group: "+str(groupID))
    if isOwner(userID, groupID):
        deleteGroup(db, groupID)
    else:
        leaveGroup(userID, groupID)
