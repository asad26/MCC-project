MCC project backend

Our project backend is a Python server, that uses Bottle library to listen for HTTP POST and GET requests. The requests can be used to manage a Firebase database. For this we used Pyrebase library and Firebase-admin library.

Instructions:

To test locally, simply use the requirements.txt to install all the libraries in a virtualenv. This can be done by using the following command:
"virtualenv --no-site-packages --distribute .env && source .env/bin/activate && pip install -r requirements.txt"
After this simply run "python socket-server.py", and your local server is running.

To deploy to Google Cloud AppEngine, simply move this backend folder to the Management Instance, enable Google APIs, set your gcloud project and run command "gcloud app deploy". To use the cron job that is provided here (deletes expired groups every 5 minutes), run command "gcloud app deploy cron.yaml".


Files included in this folder:

.gitignore

* added to ignore virtualenv-folder, logs and pycache etc.

app.yaml

* contains gcloud app data needed to deploy to gcloud

cron.yaml

* contains a cron job for gcloud, that sends HTTP GET request to the server every 5 minutes

requirements.txt

* requirements for the virtualenv
* to start virtualenv with needed requirements, "virtualenv --no-site-packages --distribute .env && source .env/bin/activate && pip install -r requirements.txt" should work

socket-server.py

* runs a local server using bottle-library
* sending a HTTP POST request to /<path> runs function matching the <path>
* deleteExpired also accepts HTTP GET requests, to accept Google Cloud AppEngine cron job calls

functions/checkAuth.py

* includes a function to check if user is authenticated

functions/createGroup.py

* includes a function to create group with given arguments

functions/deleteExpired.py

* function for checking and deleting expired groups and their pictures

functions/joinGroup.py

* function for joining a group with a given qr-token

functions/leaveOrDeleteGroup.py

* function for leaving a group if user is a member of group and deleting if the user is group owner

functions/processPicture.py

* function for processing a picture (checking for faces, re-scaling etc.)

functions/config/mcc-fall-2017-g18-firebase-adminsdk-o1mru-8ac45de714.json

* includes firebase projects service account data

functions/config/config.py

* includes apiKey, authDomain, databaseURL, storageBucket and ServiceAccount -file data

tests/

* contains a test for each of the functions described above
