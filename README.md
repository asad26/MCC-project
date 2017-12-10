# MCC event-based project

## Firebase access rule configuration:
### Firebase Realtime Database
The realtime database only allows the members of a group to access the group
folder or the users of the group. Write access to the users own entry has
been added so that the client can create it during account creation. After
this the data cannot be changed. The rest of the writes happen from the
backend using a service account, which is not covered by these rules.

    {
      "rules": {
        "groups": {
          "$groupID": {
            ".read": "root.child('users').child(auth.uid).child('groupID').val() == $groupID
                      && 1000*data.child('expiry').val() + 10*60*1000 > now",
            ".write": "false"
          }
        },
        "users":{
          "$userID": {
            ".read": "$userID == auth.uid ||
                      root.child('users').child(auth.uid).child('groupID').val() ==
                      root.child('users').child($userID).child('groupID').val()",
            ".write": "$userID == auth.uid
                       && !data.exists()"
          }
        }
      }
    }

### Firebase Storage
Users upload the pictures directly to Firebase storage. This was deemed to
be the best option, because then we can take advantage of the Firebase
file uploading capabilities. Authorization is performed during uploading
based on the upload path and the claims embedded in the user token when
the user joined the group. As the token is signed by the server, we can
guarantee the accuracy of the information. Unfortunately there is no easy
way of revoking the token, instead the token carries the group expiration
time with it, so the expiration can be recognized. Unfortunately this does
not help in the situation when the group is deleted before expiration.
In that case the app has to proactively prevent the upload, if we want
to save user bandwidth. Otherwise the server running in app engine will
just not process the file after the upload is complete.

    service firebase.storage {
      match /b/{bucket}/o {
        match /pictures/{groupID}/{userID}/{imageID} {
          allow read: if request.auth.token.groupID == groupID
          allow write: if request.auth.token.groupID == groupID
                       && request.auth.uid == userID
                       && resource == null
                       && request.resource != null
                       && int(1000*float(request.auth.token.groupExpiry)) > request.time.toMillis()
                       && request.resource.size < 100 * 1000 * 1000;
        }
        match /{allPaths=**} {
          allow read, write: if false;
        }
      }
    }
