MCC event-based project

Frontend:

* The user first need to register into this application. We used Firebase SDK Email and password based authentication in which the users are authenticated with their email addresses and passwords. This is handled in the MainActivity.java class.

	- When the user is registered, the information is stored in realtime database under /users/<userID>/ path, then user name, email, and groupID is stored as child elements.
	- These users parameters are defined in User.java class.

* We use OkHttpClient libarary to communicate with the backend. All the functinalities are handled using asynchronous post call. ApiForBackend.java defines that functinality.
	
* For storing the group pictures information locally, we used Room Persistence database library. It provides an abstraction layer over SQLite to allow fluent database access. Other classes:

	- AppDatabase.java: Defines a class for room database builder and start an instance of it. Contains the database holder and serves as the main access point for the underlying connection to your app's data.
	- PictureInfo.java: An entity with represent a table within this database.
	- PicturesInfoDao: An interface containing the methods used for accessing the database. 

* GridActivity.java displays the dashboard where button listeners are set to start the respective activity view. Other functions include:

	- Check and grant storage/camera permissions.
	- Start download service for downloding group images in the background.
	- Check whether the user belongs to some group.
	- Check network status and the image quality set in thee app settings
	- Barcode detection in the background and save pictures to local storage with Context.MODE_PRIVATE, if not barcode the picture is saved in the Firebase storage and the URL is sent to the backend.\
	
* GalleryActivity.java is used for displaying the private and group albums. Other classes are:

	- PhotoAlbum.java: for storing information regrding album (name, count, thumbnail)
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
	- ViewGroup.java: An activity for viewving the group information after the user join or create a group. Also manages leave and add member functinality.
	- AddUserActivity.java: For adding the user. Getting the server side token and making a QR code.
	- CreateGroup.java: When the user create a group inistailly.
	
* SettingsActivity.java: Manages the app settings using shared prefernces.
	
	
