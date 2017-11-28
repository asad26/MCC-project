import http.client, urllib.parse
import pyrebase
import json
import logging

logging.basicConfig(filename='logs/server-test.log',format='%(asctime)s %(message)s', level=logging.DEBUG)

logging.debug("Running group creation test")

email="paavo923@hotmail.com"
password="salasana"

config = {
    "apiKey": "AIzaSyDr49kPUF-KlIai8uufSfnFWWy3OZvyMBY",
    "authDomain": "test-project-51580.firebaseapp.com",
    "databaseURL": "https://test-project-51580.firebaseio.com",
    "projectId": "test-project-51580",
    "storageBucket": "test-project-51580.appspot.com",
    "messagingSenderId": "752406124013"
  }

logging.debug("Initializing firebase config")
firebase = pyrebase.initialize_app(config)
logging.debug("Authorizing firebase")
auth = firebase.auth()
user=auth.sign_in_with_email_and_password(email, password)


logging.debug("Initializing group data")
data = {"groupname":"myGroup", "username":"Paavo", "timeToLive":100,"userToken": user['idToken']}
jsonData = json.dumps(data)


headers = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain"}
logging.debug("Initializing HTTPConnection")
conn = http.client.HTTPConnection("localhost", 8080)
logging.debug("Sending POST request")
conn.request("POST", "/create-group", jsonData, headers)
logging.debug("Receiving response")
response = conn.getresponse()
print(response.status, response.reason)
data = response.read()
print(data)
logging.debug("Group data received: "+str(data))
conn.close()
