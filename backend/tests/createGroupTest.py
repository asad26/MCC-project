import requests
import pyrebase
import json
import logging
import urllib

logging.basicConfig(filename='/tmp/server-test.log',format='%(asctime)s %(message)s', level=logging.DEBUG)

logging.debug("Running group creation test")

email="testuser@gmail.com"
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
data = {"groupname":"myGroup", "username":"TestUser", "timeToLive":10,"userToken": user['idToken']}
postData = urllib.parse.urlencode(data)


headers = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain"}

logging.debug("Sending POST request")
logging.debug("Receiving response")
#response = requests.post('https://mcc-fall-2017-g18.appspot.com/createGroup', data=postData)
response = requests.post('http://localhost:8080/createGroup', data=postData)
print(response.text)
logging.debug("Group data received: "+str(data))
