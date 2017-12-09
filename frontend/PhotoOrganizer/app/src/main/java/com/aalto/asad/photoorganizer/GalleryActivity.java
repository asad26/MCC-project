package com.aalto.asad.photoorganizer;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asad on 11/26/2017.
 */

public class GalleryActivity extends AppCompatActivity {

    private static final String TAG = "MCC";

    private GridView gridView;
    private AlbumAdapter adapter;
    private GridActivity gridActivity;
    private List<PhotoAlbum> albumList;
    public static List<String> imagesPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "GalleryActivity:onCreate");
        setContentView(R.layout.gallery_view);
        gridView = (GridView) findViewById(R.id.grid_view);

        gridActivity = new GridActivity();
        albumList = new ArrayList<PhotoAlbum>();
        imagesPath = new ArrayList<String>();

        File[] images = gridActivity.loadImagesFromDirectory(GalleryActivity.this);

        adapter = new AlbumAdapter(GalleryActivity.this, albumList);
        gridView.setAdapter(adapter);

        if (images.length == 0) {
            Log.i(TAG, "No private pictures");
            Toast.makeText(GalleryActivity.this, "No private pictures", Toast.LENGTH_LONG).show();
        }
        else {
            for (File aListFile : images) {
                imagesPath.add(aListFile.getAbsolutePath());
            }
            String thumbnail = imagesPath.get(imagesPath.size() - 1);
            prepareAlbum("Private", String.valueOf(imagesPath.size()), thumbnail, R.drawable.not_cloud);
        }


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //if (position == 0) {
                Toast.makeText(GalleryActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(GalleryActivity.this, PrivateImageActivity.class));
                //}
            }
        });
    }

    public void prepareAlbum(String name, String numOfPhotos, String thumbnail, int pic) {
        PhotoAlbum a = new PhotoAlbum(name, numOfPhotos,thumbnail, pic);
        albumList.add(a);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "GalleryActivity:onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "GalleryActivity:onResume");
        //imagesPath.clear();
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "GalleryActivity:onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "GalleryActivity:onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "GalleryActivity:onDestroy");
    }
}
