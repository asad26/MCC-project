import http.client, urllib.parse
import pyrebase
import json

email="paavo923@hotmail.com"
password="salasana"

config = {
    "apiKey": "AIzaSyDr49kPUF-KlIai8uufSfnFWWy3OZvyMBY",
    "authDomain": "test-project-51580.firebaseapp.com",
    "databaseURL": "https://test-project-51580.firebaseio.com",
    "storageBucket": "test-project-51580.appspot.com"
}

firebase = pyrebase.initialize_app(config)
auth = firebase.auth()
user=auth.sign_in_with_email_and_password(email, password)



data = {"groupname":"myGroup", "username":"Paavo","userToken": user['idToken']}
jsonData = json.dumps(data)

#params = urllib.parse.urlencode({'@number': 12524, '@type': 'issue', '@action': 'show'})
headers = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain"}
conn = http.client.HTTPConnection("localhost", 8080)
conn.request("POST", "/create-group", jsonData, headers)
response = conn.getresponse()
print(response.status, response.reason)
data = response.read()
data
conn.close()
