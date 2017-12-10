MCC project backend


Files included in this folder:

.gitignore

* added to ignore virtualenv-folder, logs and pycache etc.

requirements.txt

* requirements for the virtualenv
* to start virtualenv with needed requirements, "virtualenv --no-site-packages --distribute .env && source .env/bin/activate && pip install -r requirements.txt" should work

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

socket-server.py

* runs a local server using bottle-library
* sending a HTTP POST request to /<path> runs function matching the <path>
* deleteExpired also accepts HTTP GET requests, to accept Google Cloud AppEngine cron job calls

tests/

* contains a test for each of the functions described above
