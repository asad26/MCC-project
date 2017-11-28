import pyrebase
import json
from testconfig import config
from checkAuth import user_is_authenticated
import sys
import logging

logging.basicConfig(filename='logs/server.log',format='%(asctime)s %(message)s', level=logging.DEBUG)


firebase = pyrebase.initialize_app(config)
db = firebase.database()

arguments=json.loads(sys.argv[1])

def create_group(groupname, user, timeToLive, usertoken):
    if user_is_authenticated(usertoken):
        logging.debug("Creating group data")
        data = {
            "owner": user,
            "name": groupname
        }
        logging.debug("Pushing group to database")
        result = db.child("groups").push(data)
        returnData = {
            "groupID":result["name"],
            "QRtoken":"notImplemented"
        }
        logging.debug("Returning group data")
        print(json.dumps(returnData),end='')
        #TODO return the qr token
    else:
        print("User authentication failed")

def get_expiry(timeToLive):
    return 1

create_group(arguments["groupname"],arguments["username"],100,arguments["userToken"])
