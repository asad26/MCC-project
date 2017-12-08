import http.client, urllib.parse
import pyrebase
import logging

logging.basicConfig(filename='logs/server-test.log',format='%(asctime)s %(message)s', level=logging.DEBUG)

logging.debug("Running leave or delete group test")

email="testman2@gmail.com"
password="password"

config = {
    "apiKey": "AIzaSyDkLUFoxI8Hh9luv8V4UKTPmE9N0GBYuQg",
    "authDomain": "mcc-fall-2017-g18.firebaseapp.com",
    "databaseURL": "https://mcc-fall-2017-g18.firebaseio.com",
    "projectId": "mcc-fall-2017-g18",
    "storageBucket": "mcc-fall-2017-g18.appspot.com",
    "messagingSenderId": "1028528125996"
  }

logging.debug("Initializing firebase config")
firebase = pyrebase.initialize_app(config)
logging.debug("Authorizing firebase")
auth = firebase.auth()
user=auth.sign_in_with_email_and_password(email, password)

logging.debug("Initializing group data")
data = {"userToken": user['idToken']}
postData = urllib.parse.urlencode(data)
headers = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain"}
logging.debug("Initializing HTTPConnection")
conn = http.client.HTTPConnection("localhost", 8080)
logging.debug("Sending POST request")
conn.request("POST", "/leaveOrDeleteGroup", postData, headers)
logging.debug("Receiving response")
response = conn.getresponse()
print(response.status, response.reason)
data = response.read()
print(data)
logging.debug("Group data received: "+str(data))
conn.close()
