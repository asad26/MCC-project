package com.aalto.asad.photoorganizer;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
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
    private List<PhotoAlbum> albumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_view);
        gridView = (GridView) findViewById(R.id.grid_view);

        //albumList = new ArrayList<>();
        adapter = new AlbumAdapter(GalleryActivity.this, GridActivity.albumList);
        gridView.setAdapter(adapter);

        //prepareAlbums();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position == 0) {
                    Toast.makeText(GalleryActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(GalleryActivity.this, PrivateImageActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        GridActivity.albumList.clear();
    }

    private void prepareAlbums() {

        PhotoAlbum a = new PhotoAlbum("Romance", "13", R.drawable.group_management);
        albumList.add(a);

        a = new PhotoAlbum("Event", "8", R.drawable.camera);
        albumList.add(a);

        adapter.notifyDataSetChanged();
    }
}
