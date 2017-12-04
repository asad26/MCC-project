package com.aalto.asad.photoorganizer;

import android.app.Activity;
import android.content.Context;
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
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

/**
 * Created by Asad on 11/8/2017.
 */

public class GridActivity extends AppCompatActivity {

    private static final String TAG = "MCC";
    private static final int CAPTURE_IMAGE = 1;
    private static boolean isBarcode = true;

    private Button groupManagement;
    private Button signOut;
    private Button buttonCamera;
    private Button buttonSettings;

    private ImageView testImage;
    private TextView testDisplay;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;
    private ValueEventListener copyPostListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_menu);

        groupManagement = (Button) findViewById(R.id.buttonGroup);
        signOut = (Button) findViewById(R.id.signOut);
        buttonCamera = (Button) findViewById(R.id.buttonCamera);
        buttonSettings = (Button) findViewById(R.id.buttonSettings);

        // Test
        testImage = (ImageView) findViewById(R.id.imageViewTest);
        testDisplay = (TextView) findViewById(R.id.textView);

        mFirebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(mFirebaseUser.getUid());

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
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, CAPTURE_IMAGE);
                //}
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
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                //testImage.setImageBitmap(imageBitmap);
                ProcessImages pImages = new ProcessImages();
                pImages.execute(getResizedBitmap(imageBitmap, 300));
            } else {
                Toast.makeText(GridActivity.this, "Failed to capture image", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class ProcessImages extends AsyncTask<Bitmap, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getApplicationContext())
                    .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                    .build();

            Frame frame = new Frame.Builder().setBitmap(bitmaps[0]).build();
            SparseArray<Barcode> barcode = barcodeDetector.detect(frame);

            if(!barcodeDetector.isOperational()){
                Log.i(TAG, "Could not set up the detector!");
                IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;

                if (hasLowStorage) {
                    Log.i(TAG, getString(R.string.low_storage_error));
                }
            }

            int size = barcode.size();
            if (size != 0) {
                isBarcode = true;
                Log.i(TAG, "This image is barcode");
                barcodeDetector.release();
                barcode.clear();
                return bitmaps[0];
            }
            else {
                isBarcode = false;
                return bitmaps[0];
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (isBarcode) {
                // Save image in the private folder
            } else {
                // Send image to the cloud
                testImage.setImageBitmap(bitmap);
            }
            Log.i(TAG, "Image successfully processed and displayed");
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    public void onStart() {
        super.onStart();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, "User Name: " + user.userName);
                testDisplay.setText(user.userName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postListener:onCancelled ", databaseError.toException());
            }
        };
        mDatabaseReference.addValueEventListener(postListener);
        copyPostListener = postListener;

        //Get a reference to shared preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //Find a preference value by key
        String mdataSync = sharedPref.getString("pref_key_mdata_sync", "");
        String wifiSync = sharedPref.getString("pref_key_wifi_sync", "");
        ((TextView)findViewById(R.id.textView9)).setText("Mobile data sync set to: "+mdataSync);
        ((TextView)findViewById(R.id.textView8)).setText("Wifi sync set to: "+wifiSync);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (copyPostListener != null) {
            mDatabaseReference.removeEventListener(copyPostListener);
        }
    }
}
