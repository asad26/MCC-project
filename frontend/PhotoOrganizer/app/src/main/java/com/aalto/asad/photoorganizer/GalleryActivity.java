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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asad on 11/26/2017.
 */

public class GalleryActivity extends AppCompatActivity {

    private static final String TAG = "MCC";

    private GridView gridView;
    private AlbumAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "GalleryActivity:onCreate");
        setContentView(R.layout.gallery_view);
        gridView = (GridView) findViewById(R.id.grid_view);

        adapter = new AlbumAdapter(GalleryActivity.this, GridActivity.albumList);
        gridView.setAdapter(adapter);

        gridListener();
    }

    private void gridListener() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //if (position == 0) {
                    Toast.makeText(GalleryActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(GalleryActivity.this, PrivateImageActivity.class));
                //}
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "GalleryActivity:onStart");
        gridListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "GalleryActivity:onResume");
        //startActivity(new Intent(GalleryActivity.this, GalleryActivity.class));
        //this.onCreate(null);
        gridListener();
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "GalleryActivity:onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        GridActivity.albumList.clear();
        GridActivity.imagesPath.clear();
        Log.d(TAG, "GalleryActivity:onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "GalleryActivity:onDestroy");
    }
}
