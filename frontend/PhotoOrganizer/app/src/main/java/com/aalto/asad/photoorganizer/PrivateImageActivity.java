package com.aalto.asad.photoorganizer;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asad on 12/3/2017.
 */

public class PrivateImageActivity extends AppCompatActivity {

    private static final String TAG = "MCC";

    private GridView privateGridView;
    private ImageAdapter adapter;
    private ArrayList<String> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "PrivateImageActivity:onCreate");
        setContentView(R.layout.private_gallery_view);
        privateGridView = (GridView) findViewById(R.id.private_grid_view);
        imageList = new ArrayList<String>();

        final ImageAdapter adapter = new ImageAdapter(PrivateImageActivity.this, GalleryActivity.privateImages);

        privateGridView.setAdapter(adapter);

        privateGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(PrivateImageActivity.this, PhotoViewActivity.class);
                Bundle b = new Bundle();
                b.putString("path", GalleryActivity.privateImages.get(position)); //Image path
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "PrivateImageActivity:onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "PrivateImageActivity:onResume");
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "PrivateImageActivity:onPause");
    }


    @Override
    public void onStop() {
        super.onStop();
        imageList.clear();
        Log.d(TAG, "PrivateImageActivity:onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        imageList.clear();
        Log.d(TAG, "PrivateImageActivity:onDestroy");
    }

//    public void loadImagesFromDirectory() {
//        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
//        File directory = contextWrapper.getDir("imagePrivate", Context.MODE_PRIVATE);
//        File[] listFile = directory.listFiles();
//
//        for (File aListFile : listFile) {
//            imageList.add(aListFile.getAbsolutePath());
//            Log.i(TAG, "Image path in Private Activity: " + aListFile.getAbsolutePath());
//        }
//    }
}
