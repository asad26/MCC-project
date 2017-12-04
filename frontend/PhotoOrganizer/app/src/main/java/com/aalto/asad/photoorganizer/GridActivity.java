package com.aalto.asad.photoorganizer;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import java.util.List;
import java.util.Locale;

/**
 * Created by Asad on 11/8/2017.
 */

public class GridActivity extends AppCompatActivity {

    private static final String TAG = "MCC";
    private static final int CAPTURE_IMAGE = 1;
    //private static boolean isBarcode = true;

    private Button groupManagement;
    private Button signOut;
    private Button buttonCamera;
    private Button buttonSettings;

    private Button imagesGallery;

    // Firebase instances
    private StorageReference storageReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabaseReference;
    private ValueEventListener copyPostListener;

    private File imageFile = null;
    private String userToken;
    public static List<PhotoAlbum> albumList;

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
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(mFirebaseUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference();

        albumList = new ArrayList<PhotoAlbum>();


            sendData("http://192.168.43.167:8080/", "Hello World!");

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
                int c = countImagesFromDirectory();
                PhotoAlbum a = new PhotoAlbum("Private", String.valueOf(c), R.drawable.group_management);
                albumList.add(a);
                Intent intent = new Intent(GridActivity.this, GalleryActivity.class);
                startActivity(intent);
            }
        });

        groupManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent groupManagementIntent = new Intent(getApplicationContext(), ViewGroup.class);
                startActivity(groupManagementIntent);
            }
        });

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    try {
                        imageFile = createImageFile();
                        Log.i(TAG, "createImageFile:success");
                    } catch (IOException e) {
                        Log.i(TAG, "createImageFile:failure " + e.getMessage());
                    }

                    if (imageFile != null) {
                        Log.i(TAG, "button camera Image file ");
                        Uri imageURI = FileProvider.getUriForFile(getApplicationContext(), "com.android.fileprovider", imageFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE);
                    }
                }
            }
        });

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = Uri.fromFile(new File(imageFile.getAbsolutePath()));
                ProcessImages processImages = new ProcessImages();
                processImages.execute(imageUri);
            } else {
                Toast.makeText(GridActivity.this, "Failed to capture image", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class ProcessImages extends AsyncTask<Uri, Void, Boolean> {

        Bitmap imageBitmap;
        Uri imageUri;

        @Override
        protected Boolean doInBackground(Uri... uris) {
            BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getApplicationContext())
                    .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                    .build();

            imageUri = uris[0];
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Frame frame = new Frame.Builder().setBitmap(imageBitmap).build();
            SparseArray<Barcode> barcodes = barcodeDetector.detect(frame);

            if(!barcodeDetector.isOperational()){
                Log.i(TAG, "Could not set up the detector!");
                IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;
                if (hasLowStorage) {
                    Log.i(TAG, "lessStorage: " + getString(R.string.low_storage_error));
                }
            }

            int size = barcodes.size();
            if (size != 0) {
                Log.i(TAG, "This image has a barcode");
                barcodeDetector.release();
                barcodes.clear();
                return true;
            }
            else {
                Log.i(TAG, "This image is not barcode");
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isBarcode) {
            super.onPostExecute(isBarcode);
            if (isBarcode) {
                saveImageToStorage(imageBitmap);
                Toast.makeText(GridActivity.this, "This is barcode ",
                        Toast.LENGTH_LONG).show();
            } else {
                //Store in a Firebase storage
                StorageReference imageReference = storageReference.child("photos").child(imageFile.getName());
                UploadTask uploadTask = imageReference.putFile(imageUri);

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
                        Log.i(TAG, "image URL " + downloadUrl);
                        Toast.makeText(GridActivity.this, "Picture uploaded", Toast.LENGTH_LONG).show();
                    }
                });

            }
            Log.i(TAG, "Image successfully processed and displayed");
        }
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        ValueEventListener postListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//                Log.d(TAG, "User Name: " + user.userName);
//                testDisplay.setText(user.userName);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.w(TAG, "postListener:onCancelled ", databaseError.toException());
//            }
//        };
//        mDatabaseReference.addValueEventListener(postListener);
//        copyPostListener = postListener;
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        // Remove post value event listener
//        if (copyPostListener != null) {
//            mDatabaseReference.removeEventListener(copyPostListener);
//        }
//    }

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


    private static void sendData(String url, String data) {
        HttpURLConnection httpcon = null;
        OutputStream os =  null;
        try {
            httpcon = (HttpURLConnection) ((new URL(url).openConnection()));
            httpcon.setDoOutput(true);
            httpcon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpcon.setRequestProperty("Accept", "text/plain");
            httpcon.setRequestMethod("POST");
            byte[] outputBytes = data.getBytes("UTF-8");
            Log.d(TAG, "output bytes " + outputBytes.toString());
            os = httpcon.getOutputStream();
            Log.d(TAG, "output bytes " + httpcon.getContent());
            os.write(outputBytes);
            Log.d(TAG, "send successed: " + httpcon.getResponseMessage());
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            Log.d(TAG, "send error: " + e.getMessage());
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (httpcon != null) {
                httpcon.disconnect();
            }
        }
    }

//    private static void sendOdfData(String url, String finalMessage) throws IOException {
//        HttpClient client = HttpClientBuilder.create().build();
//        HttpPost post = new HttpPost(url);
//
//        HttpEntity entity = new ByteArrayEntity(finalMessage.getBytes("UTF-8"));
//        post.addHeader("Content-Type", "application/x-www-form-urlencoded");
//        post.setEntity(entity);
//        HttpResponse response = client.execute(post);
//        String result = EntityUtils.toString(response.getEntity());
//        Log.d(TAG, "send successed: " + result);
//        //System.out.println(response.getStatusLine());
//    }

    @Override
    public void onStart() {
        super.onStart();
        //Get a reference to shared preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //Find a preference value by key
        String mdataSync = sharedPref.getString("pref_key_mdata_sync", "");
        String wifiSync = sharedPref.getString("pref_key_wifi_sync", "");
        ((TextView)findViewById(R.id.textView9)).setText("Mobile data sync set to: "+mdataSync);
        ((TextView)findViewById(R.id.textView8)).setText("Wifi sync set to: "+wifiSync);
    }

    public int countImagesFromDirectory() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir("imagePrivate", Context.MODE_PRIVATE);
        File[] listFile = directory.listFiles();
        return listFile.length;
    }
}
