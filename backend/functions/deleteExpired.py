import pyrebase
from config.config import config
import logging
from time import time


def main(kwargs_dict):
    logging.basicConfig(filename='logs/server.log',format='%(asctime)s %(message)s', level=logging.DEBUG)
    firebase = pyrebase.initialize_app(config)
    db = firebase.database()
    logging.debug("Looking for expired groups")
    print("Looking for expired groups")
    fb_storage = firebase.storage()
    return deleteExpired(db, fb_storage)

def deleteExpired(db, fb_storage):
    groups = db.child("groups").get()
    if(str(groups.val())=='None'):
        return "No groups"
    deletionHistory = ''
    for group in groups.each():
        if 'expiry' in group.val():
            expiry = group.val()['expiry']
            if expiry<time():
                groupID = str(group.key())
                deletionHistory += deleteGroup(db, fb_storage, groupID)
    return deletionHistory

def deleteGroup(database, fb_storage, groupID):
    #Remove group id from every member
    members = database.child("groups").child(groupID).child("members").get()
    imagePaths = []
    for member in members.each():
        uid = member.key()
        database.child("users").child(uid).child("groupID").remove()
    #delete images related to group
    deleteAllImages(database, fb_storage, groupID)
    logging.debug("Deleting group: "+groupID)
    database.child("groups").child(groupID).remove()
    return "Removed group "+groupID+"\n"

def deleteAllImages(db, fb_storage, groupID):
    picturePaths = []
    pictures = db.child("groups").child(groupID).child("pictures").get()
    if str(pictures.each()) == "None":
        return
    for picture in pictures.each():
        data = picture.val()
        picturePaths.append(str(data["picture"]))
        picturePaths.append(str(data["picture_1280"]))
        picturePaths.append(str(data["picture_640"]))
    #remove possible duplicates
    picturePaths = list(set(picturePaths))
    logging.debug("Going to delete these pictures: "+str(picturePaths))
    for picturePath in picturePaths:
        logging.debug("Removing picture: "+str(picturePath))
        fb_storage.delete(picturePath)

if __name__ == "__main__":
    main(None)
