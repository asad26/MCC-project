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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asad on 12/3/2017.
 */

public class PrivateImageActivity extends AppCompatActivity {

    private static final String TAG = "MCC";

    private GridView privateGridView;
    //private ImageAdapter adapter;
    private List<String> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.private_gallery_view);
        privateGridView = (GridView) findViewById(R.id.private_grid_view);
        imageList = new ArrayList<String>();

        LoadImages loadImages = new LoadImages();
        loadImages.execute();
        //loadImagesFromDirectory();
    }

    class LoadImages extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageList.clear();
        }

        @Override
        protected Void doInBackground(Void... aVoid) {
            loadImagesFromDirectory();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ImageAdapter adapter = new ImageAdapter(getApplicationContext(), imageList);
            privateGridView.setAdapter(adapter);
            Log.d(TAG, "Adapter has been set ");
        }
    }

    public void loadImagesFromDirectory() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir("imagePrivate", Context.MODE_PRIVATE);

        File[] listFile = directory.listFiles();

        for (File aListFile : listFile) {
            imageList.add(aListFile.getAbsolutePath());
            Log.d(TAG, "Image path in Private Activity: " + aListFile.getAbsolutePath());
        }

        //adapter.notifyDataSetChanged();
    }
}
