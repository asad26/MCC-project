package com.aalto.asad.photoorganizer;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Asad on 11/8/2017.
 */

public class GridActivity extends AppCompatActivity {

    private static final String TAG = "MCC";
    private static final int CAPTURE_IMAGE = 1;
    //private static final int ANDROID_CAMERA_REQUEST_CODE = 100;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 2;
    private static boolean isBarcode = true;

    private static final int TYPE_WIFI = 100;
    private static final int TYPE_MOBILE_DATA = 101;
    private static final int TYPE_NO_NETWORK = 102;

    private Button groupManagement;
    private Button signOut;
    private Button buttonCamera;
    private Button buttonSettings;

    private Button imagesGallery;

    // Firebase instances
    private StorageReference storageReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mUserGroupsReference;
    private ValueEventListener mUserGroupsListener;
    private ValueEventListener copyPostListener;
    private String userID;
    private String groupID;

    private File imageFile = null;
    private String userToken;
    public static List<PhotoAlbum> albumList;
    private HashMap<String, String> params;
    ApiForBackend api;
    public static List<String> imagesPath;

    /** This is hot to read the sync preferences
    //Get a reference to shared preferences
    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    //Find a preference value by key
    String mdataSync = sharedPref.getString("pref_key_mdata_sync", "");
    String wifiSync = sharedPref.getString("pref_key_wifi_sync", "");
    String groupID = sharedPref.getString("group_id", "");
    */

    // For settings preference
    private SharedPreferences sharedPref;
    private String mdataSync;
    private String wifiSync;

    private int networkStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_menu);

        groupManagement = (Button) findViewById(R.id.buttonGroup);
        signOut = (Button) findViewById(R.id.logOut);
        buttonCamera = (Button) findViewById(R.id.buttonCamera);
        buttonSettings = (Button) findViewById(R.id.buttonSettings);
        imagesGallery = (Button) findViewById(R.id.buttonGallery);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        userID = mFirebaseUser.getUid();
        mUserGroupsReference = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
        storageReference = FirebaseStorage.getInstance().getReference();

        imagesPath = new ArrayList<String>();
        albumList = new ArrayList<PhotoAlbum>();

        params = new HashMap<String, String>();
        api = new ApiForBackend();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(GridActivity.this);

        // Retrieve user ID token
        mFirebaseUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    userToken = task.getResult().getToken();
                    Log.d(TAG, "userToken:success ");
                } else {
                    Log.d(TAG, "userToken:failure " + task.getException().getMessage());
                }
            }
        });


        imagesGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String rrr = ap.executePost("createGroup", params);
                //Log.d(TAG, "response " + rrr);
                //api.executePost("create-group", params);

                loadImagesFromDirectory();
                String thumbnail = imagesPath.get(imagesPath.size() - 1);
                PhotoAlbum a = new PhotoAlbum("Private", String.valueOf(imagesPath.size()), thumbnail, R.drawable.not_cloud);
                albumList.add(a);
                Intent intent = new Intent(GridActivity.this, GalleryActivity.class);
                startActivity(intent);
            }
        });

        groupManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupID.equals("")) {
                    Intent groupManagementIntent = new Intent(getApplicationContext(), GroupManagement.class);
                    startActivity(groupManagementIntent);
                } else {
                    Intent viewGroupIntent = new Intent(getApplicationContext(), ViewGroup.class);
                    viewGroupIntent.putExtra("Group", groupID);
                    startActivity(viewGroupIntent);
                }
            }
        });

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkStatus = checkNetworkStatus(GridActivity.this);
                Log.d(TAG, "network status " + networkStatus);
                //if (groupID.isEmpty()) {
                //    Toast.makeText(GridActivity.this, "Join or create a group first!", Toast.LENGTH_LONG).show();
               // } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if(checkAndRequestPermissions()) {
                            takePictureIntent();
                        }
                    } else {
                        takePictureIntent();
                    }
              //  }
            }
        });

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                OkHttpClient okHttpClient = new
//                ApiForBackend ap = new ApiForBackend();
//                params.put("groupname", "Picnic");
//                params.put("username", "Asad");
//                params.put("timeToLive", "10");
//                params.put("userToken", userToken);
//                Request r = ap.executePost("/createGroup", params);


                Intent settingIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingIntent);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuth.signOut();
                Log.d(TAG, "signOut:success");
                Intent intent = new Intent(GridActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        ValueEventListener userGroupsListener = new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                if((!dataSnapshot.hasChild("groupID")) || dataSnapshot.child("groupID") == null) {
                    User user = dataSnapshot.getValue(User.class);
                    Log.i(TAG, "Checking user group, none found");
                    groupID = "";
                } else {
                    User user = dataSnapshot.getValue(User.class);
                    groupID = user.getGroupID();
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(GridActivity.this);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("group_id", groupID);
                    editor.commit();
                    Log.i(TAG, "Checking user group, found: " + sharedPref.getString("group_id", ""));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "userGroupListener:onCancelled");
                Toast.makeText(GridActivity.this, "Unable to read user's group from database.", Toast.LENGTH_LONG).show();
            }
        };
        mUserGroupsListener = userGroupsListener;
        mUserGroupsReference.addListenerForSingleValueEvent(userGroupsListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mUserGroupsReference.removeEventListener(mUserGroupsListener);
    }

    private boolean checkAndRequestPermissions() {
        int cameraPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int writePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(GridActivity.this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.i(TAG, "Permission request callback");
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {

            Map<String, Integer> perms = new HashMap<>();
            // Initialize the map with permissions
            perms.put(android.Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
            perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(android.Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

            // Fill with actual results from user
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }

                if ((perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                        && (perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                        && (perms.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {

                    Log.i(TAG, "Camera and Storage Permissions granted");
                    Toast.makeText(this, "Camera and Storage Permissions granted", Toast.LENGTH_LONG).show();
                    takePictureIntent();

                } else {
                    Log.i(TAG, "Permissions are not granted. ");
                    Toast.makeText(this, "Permissions are not granted! Ask again", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void takePictureIntent() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            try {
                imageFile = createImageFile();
                Log.i(TAG, "createImageFile:success");
            } catch (IOException e) {
                Log.i(TAG, "createImageFile:failure " + e.getMessage());
            }

            if (imageFile != null) {
                Log.i(TAG, "button camera Image file ");
                Uri imageURI = FileProvider.getUriForFile(getApplicationContext(), "com.android.fileprovider", imageFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                startActivityForResult(cameraIntent, CAPTURE_IMAGE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = Uri.fromFile(new File(imageFile.getAbsolutePath()));
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    ProcessImages processImages = new ProcessImages();
                    processImages.execute(imageBitmap);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            } else {
                Toast.makeText(GridActivity.this, "Failed to capture image", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class ProcessImages extends AsyncTask<Bitmap, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getApplicationContext()).build();
            Frame frame = new Frame.Builder().setBitmap(bitmaps[0]).build();
            SparseArray<Barcode> barcode = barcodeDetector.detect(frame);

            if(!barcodeDetector.isOperational()){
                Log.i(TAG, "Could not set up the detector!");
                IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;
                if (hasLowStorage) {
                    Log.i(TAG, "lessStorage: " + getString(R.string.low_storage_error));
                }
            }

            if (barcode.size() != 0) {
                isBarcode = true;
                Log.i(TAG, "This image has a barcode");
                barcodeDetector.release();
                barcode.clear();
                return bitmaps[0];
            }
            else {
                isBarcode = false;
                Log.i(TAG, "This image is not barcode");
                return bitmaps[0];
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (isBarcode) {
                saveImageToStorage(bitmap);
                Toast.makeText(GridActivity.this, "This is barcode. Image saved", Toast.LENGTH_LONG).show();
            } else {
                String resolution = checkSettings();
                if (resolution != null) {
                    storeImageInFirebase(bitmap, resolution);
                }
            }
            Log.i(TAG, "Image successfully processed and displayed");
        }

        private String checkSettings() {
            String resolution;
            int networkStatus = checkNetworkStatus(GridActivity.this);
            //Log.d(TAG, "network status " + networkStatus);
            if (networkStatus == TYPE_WIFI) {
                resolution = sharedPref.getString("pref_key_wifi_sync", "");
            } else if (networkStatus == TYPE_MOBILE_DATA) {
                resolution = sharedPref.getString("pref_key_mdata_sync", "");
            } else {
                Log.d(TAG, "No internet connection. Please enable internet connection");
                Toast.makeText(GridActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
                resolution = null;
            }
            return resolution;
        }


        private void storeImageInFirebase(Bitmap bitmap, String resolution) {

            Bitmap newBitmap;
            switch (resolution) {
                case "Low":
                    newBitmap = getResizedBitmap(bitmap, 640, 480);
                    Log.i(TAG, "picture Size: " + newBitmap.getWidth() + " x " + newBitmap.getHeight());
                    break;
                case "High":
                    newBitmap = getResizedBitmap(bitmap, 1280, 960);
                    Log.i(TAG, "picture Size: " + newBitmap.getWidth() + " x " + newBitmap.getHeight());
                    break;
                default:
                    newBitmap = bitmap;
                    Log.i(TAG, "picture Size: " + newBitmap.getWidth() + " x " + newBitmap.getHeight());
                    break;
            }

            final String st = "pictures/qwerty/" + userID + "/" + imageFile.getName();
            StorageReference imageReference = storageReference.child(st);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(GridActivity.this.getContentResolver(), newBitmap, imageFile.getName(), null);
            UploadTask uploadTask = imageReference.putFile(Uri.parse(path));

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.i(TAG, "pictureUpload:failure " + exception.getMessage());
                    Toast.makeText(GridActivity.this, "Upload failure: " + exception.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    sendUrlToBackend(st);
                    Log.i(TAG, "image URL " + downloadUrl);
                    Toast.makeText(GridActivity.this, "Picture uploaded", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void sendUrlToBackend(String downloadUrl) {
        params.put("picture_path", downloadUrl);
        params.put("userToken", userToken);
        api.executePost("/processPicture", params);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        return image;
    }

    private void saveImageToStorage(Bitmap imageBitmap) {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir("imagePrivate", Context.MODE_PRIVATE);
        File imagePath = new File(directory, imageFile.getName());
        Log.d(TAG, "Image path: " + imagePath);
        Log.d(TAG, "directory path: " + directory);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imagePath);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Log.d(TAG, "Image is saved: " + imageFile.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "Image error: " + e.getMessage());
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void loadImagesFromDirectory() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir("imagePrivate", Context.MODE_PRIVATE);
        File[] listFile = directory.listFiles();

        for (File aListFile : listFile) {
            imagesPath.add(aListFile.getAbsolutePath());
        }
    }

    private int checkNetworkStatus(Context mContext) {

        int networkStatus;
        final ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert manager != null;
        final android.net.NetworkInfo wifi = manager.getActiveNetworkInfo();
        final android.net.NetworkInfo mobile = manager.getActiveNetworkInfo();

        if( wifi.getType() == ConnectivityManager.TYPE_WIFI) {
            networkStatus = TYPE_WIFI;
        }else if(mobile.getType() == ConnectivityManager.TYPE_MOBILE){
            networkStatus = TYPE_MOBILE_DATA;
        }else{
            networkStatus = TYPE_NO_NETWORK;
        }
        return networkStatus;
    }


    public Bitmap getResizedBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();

        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        bitmap.recycle();
        return resizedBitmap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        albumList.clear();
        imagesPath.clear();
    }
}
