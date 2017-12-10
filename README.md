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



## Frontend:

* The user first need to register into this application. We used Firebase SDK Email and password based authentication in which the users are authenticated with their email addresses and passwords. This is handled in the MainActivity.java class.

	- When the user is registered, the information is stored in realtime database under /users/<userID>/ path, then user name, email, and groupID is stored as child elements.
	- These users parameters are defined in User.java class.

* We use OkHttpClient library to communicate with the backend. All the functionalities are handled using asynchronous post call. ApiForBackend.java defines that functionality.

* For storing the group pictures information locally, we used Room Persistence database library. It provides an abstraction layer over SQLite to allow fluent database access. Other classes:

	- AppDatabase.java: Defines a class for room database builder and start an instance of it. Contains the database holder and serves as the main access point for the underlying connection to your app's data.
	- PictureInfo.java: An entity with represent a table within this database.
	- PicturesInfoDao: An interface containing the methods used for accessing the database.

* GridActivity.java displays the dashboard where button listeners are set to start the respective activity view. Other functions include:

	- Check and grant storage/camera permissions.
	- Start download service for downloading group images in the background.
	- Check whether the user belongs to some group.
	- Check network status and the image quality set in thee app settings
	- Barcode detection in the background and save pictures to local storage with Context.MODE_PRIVATE, if not barcode the picture is saved in the Firebase storage and the URL is sent to the backend.\

* GalleryActivity.java is used for displaying the private and group albums. Other classes are:

	- PhotoAlbum.java: for storing information regarding album (name, count, thumbnail)
	- AlbumAdapter.java: Custom adapter for creating album views
	- ImageAdapter.java: Custom adapter for creating private images view.
	- PrivateImageActivity.java: An activity for setting the ImageAdapter and displaying pictures.
	- PictureAlbumAdapter.java: Custom adapter for creating group pictures view.
	- PictureAlbumActivity.java: An activity for setting the PictureAlbumAdapter and displaying group pictures.
	- PictureAlbumDatabaseRead.java: A background task for reading Room database and get pictures based on group name.
	- PhotoViewActivity.java: For image zooming. We used PhotoView library
	
* A download service has been started for reading the groups picture information. That service is defined in DownloaderService.java:

	- DLparams.java: A class for defining url and destination folder parameters.
	- DownloadTask.java: A background task for downloading group pictures from Firebase and stored in a local storage.

* GroupManagement.java is an activity for handling user groups. Other classes are:

	- Group.java: Defines group information in terms of name, owner, ownerID, and expiry.
	- GroupMember.java: Parameters for defining members such as name and QR
	- ViewGroup.java: An activity for viewing the group information after the user join or create a group. Also manages leave and add member functinality.
	- AddUserActivity.java: For adding the user. Getting the server side token and making a QR code.
	- CreateGroup.java: When the user creates a group initially.

* SettingsActivity.java: Manages the app settings using shared preferences.
