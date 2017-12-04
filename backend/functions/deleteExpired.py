import pyrebase
from config.config import config
import logging
from time import time


def main():
    logging.basicConfig(filename='logs/server.log',format='%(asctime)s %(message)s', level=logging.DEBUG)
    firebase = pyrebase.initialize_app(config)
    db = firebase.database()
    logging.debug("Looking for expired groups")
    print("Looking for expired groups")
    deleteExpired(db)

def deleteExpired(db):
    groups = db.child("groups").get()
    for group in groups.each():
        if 'expiry' in group.val():
            expiry = group.val()['expiry']
            if expiry<time():
                groupID = str(group.key())
                deleteGroup(db, groupID)

def deleteGroup(database, groupID):
    #Remove group id from every member
    members = database.child("groups").child(groupID).child("members").get()
    for member in members.each():
        uid = member.key()
        database.child("users").child(uid).child("groupID").remove()
    #TODO delete images related to group
    logging.debug("Deleting group: "+groupID)
    database.child("groups").child(groupID).remove()
    print("Removed group "+groupID)

if __name__ == "__main__":
    main()
