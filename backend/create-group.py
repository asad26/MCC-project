import pyrebase
import json
from testconfig import config
from checkAuth import user_is_authenticated
import sys



firebase = pyrebase.initialize_app(config)
db = firebase.database()

arguments=json.loads(sys.argv[1])

def create_group(groupname,user, usertoken):
    # TODO check user authentication
    if user_is_authenticated(usertoken):
        data = {
            "owner": user,
            "name": groupname
        }
        result = db.child("groups").push(data)
        groupID = result["name"]
        print("Created group with id "+str(groupID))
        #TODO return the qr token
    else:
        print("User authentication failed")

create_group(arguments["groupname"],arguments["username"],arguments["userToken"])
