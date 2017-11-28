MCC project backend


Files included in this folder:

.gitignore

* added to ignore virtualenv-folder and pycache etc.

requirements.txt

* requirements for the virtualenv
* to start virtualenv with needed requirements, "virtualenv --no-site-packages --distribute .env && source .env/bin/activate && pip install -r requirements.txt" should work

functions/checkAuth.py

* includes a function to check if user is authenticated

functions/create-group.py

* includes a function to create group with given arguments

socket-server.py

* runs a local server using bottle-library
* sending a HTTP POST request to /<path> runs <path>.py

server-test.py

* an example of a HTTP POST request done by client

functions/config/test-key.json

* includes firebase projects service account data

functions/config/testconfig.py

* includes apiKey, authDomain, databaseURL, storageBucket and ServiceAccount -file data
