package com.aalto.asad.photoorganizer;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
    private static List<PhotoAlbum> albumList;
    public static List<String> privateImages;
    public static List<String> groupImages;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "GalleryActivity:onCreate");
        setContentView(R.layout.gallery_view);
        gridView = (GridView) findViewById(R.id.grid_view);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        gridActivity = new GridActivity();
        albumList = new ArrayList<PhotoAlbum>();
        privateImages = new ArrayList<String>();
        groupImages = new ArrayList<String>();

        adapter = new AlbumAdapter(GalleryActivity.this, albumList);
        gridView.setAdapter(adapter);

        String groupDirectory = sharedPref.getString("group_directory_name", "");
        Log.i(TAG, "GalleryActivity " + groupDirectory);
        if (!groupDirectory.isEmpty()) {
            String[] subs = groupDirectory.split("_");
            String albumName = subs[subs.length-1];
            String tmp = subs[0];
            String[] subs2 = tmp.split(" ");
            String groupID = subs2[subs2.length-1];
            File[] groupImagesPath = loadGroupImagesFromDirectory(GalleryActivity.this, groupDirectory);
            Log.i(TAG, "GalleryActivity length " + groupImagesPath.length);
            if (groupImagesPath.length != 0) {
                for (File aListFile : groupImagesPath) {
                    groupImages.add(aListFile.getAbsolutePath());
                }
                String thumbnail = groupImages.get(groupImages.size() - 1);
                prepareAlbum(albumName, groupID, String.valueOf(groupImages.size()), thumbnail, R.drawable.cloud);
            } else {
                Log.i(TAG, "No group pictures");
                Toast.makeText(GalleryActivity.this, "No group pictures", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.i(TAG, "No group found!");
        }

        File[] images = gridActivity.loadImagesFromDirectory(GalleryActivity.this);
        if (images.length == 0) {
            Log.i(TAG, "No private pictures");
            Toast.makeText(GalleryActivity.this, "No private pictures", Toast.LENGTH_LONG).show();
        }
        else {
            for (File aListFile : images) {
                privateImages.add(aListFile.getAbsolutePath());
            }
            String thumbnail = privateImages.get(privateImages.size() - 1);
            prepareAlbum("Private", "0", String.valueOf(privateImages.size()), thumbnail, R.drawable.not_cloud);
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String albumID = albumList.get(position).getID();
                Log.d(TAG, albumID);
                if(albumID.equals("0")){
                    Log.d(TAG, albumID);
                    startActivity(new Intent(GalleryActivity.this, PrivateImageActivity.class));
                }else{
                    Intent intent = new Intent(getApplicationContext(), PictureAlbumActivity.class);
                    intent.putExtra("Group", albumID);
                    startActivity(intent);
                }
                //if (position == 0) {
                //Toast.makeText(GalleryActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                //
                //}
            }
        });
    }

    public void prepareAlbum(String name, String albumID, String numOfPhotos, String thumbnail, int pic) {
        PhotoAlbum a = new PhotoAlbum(name, albumID, numOfPhotos,thumbnail, pic);
        albumList.add(a);
        adapter.notifyDataSetChanged();
    }

    public File[] loadGroupImagesFromDirectory(Context mContext, String directory) {
        ContextWrapper contextWrapper = new ContextWrapper(mContext);
        File storageDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_PICTURES+"/"+directory);
        Log.i(TAG, "GalleryActivity storage directory " + storageDirectory);
        File[] listFile = storageDirectory.listFiles();
        return listFile;
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.d(TAG, "GalleryActivity:onStart");
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.d(TAG, "GalleryActivity:onResume");
//        //imagesPath.clear();
//    }
//
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.d(TAG, "GalleryActivity:onPause");
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        Log.d(TAG, "GalleryActivity:onStop");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.d(TAG, "GalleryActivity:onDestroy");
//    }
}
