package com.aalto.asad.photoorganizer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PictureAlbumActivity extends AppCompatActivity implements OnTaskCompleted {

    private static final String TAG = "MCC";

    private ScrollView scrollView;
    private String groupID;
    private String groupName;
    private DatabaseReference mGroupsReference;
    private ValueEventListener mGroupListener;
    private List<PictureInfo> allPictures;
    private String sortMode;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_album);
        scrollView = findViewById(R.id.scrollableView);

        groupID = getIntent().getStringExtra("Group");
        mGroupsReference = FirebaseDatabase.getInstance().getReference().child("groups").child(groupID);
        mGroupListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Group group = dataSnapshot.getValue(Group.class);
                groupName = group.getName();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mGroupsReference.addListenerForSingleValueEvent(mGroupListener);

        PictureAlbumDatabaseRead backGroundReader = new PictureAlbumDatabaseRead(this, PictureAlbumActivity.this);
        backGroundReader.execute(groupID);
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    public void onTaskCompleted(List<PictureInfo> result) {
        Log.i(TAG, "PictureAlbumActivity:onTaskCompleted");
        allPictures = result;
        reloadPicturesIntoView();
    }
        /**mPicturesReference = FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).child("pictures");
        mPicturesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "ValueEventListener:onDataChange");
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    allPictures.add(child.getValue(Picture.class));
                }
                reloadPicturesIntoView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "ValueEventListener:onCancelled");
            }
        };
        mPicturesReference.addListenerForSingleValueEvent(mPicturesListener);*/

    private void reloadPicturesIntoView() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sortMode = sharedPref.getString("album_sort_mode", "");
        String groupDirectory = sharedPref.getString("picture_directory_"+groupID, "");
        HashMap<String, ArrayList<String>> picturesMap = new HashMap<String, ArrayList<String>>();
        if (sortMode.equals("people") || sortMode.equals("")) {
            for (int i = 0; i < allPictures.size(); i++) {
                PictureInfo picture = allPictures.get(i);
                String uri = groupDirectory+"/"+picture.getLocalUri();
                String people = picture.getContainsPeople();
                Log.d(TAG, "Contains people says: "+people);
                if (people.equals("true")) {
                    if (picturesMap.containsKey("People")) {
                        if(!picturesMap.get("People").contains(uri)) {
                            picturesMap.get("People").add(uri);
                        }
                    } else {
                        ArrayList<String> arrList = new ArrayList<String>();
                        arrList.add(uri);
                        picturesMap.put("People" ,arrList);
                    }
                } else {
                    if (picturesMap.containsKey("No people")) {
                        if(!picturesMap.get("No people").contains(uri)) {
                            picturesMap.get("No people").add(uri);
                        }
                    } else {
                        ArrayList<String> arrList = new ArrayList<String>();
                        arrList.add(uri);
                        picturesMap.put("No people" ,arrList);
                    }
                }
            }
        } else {
            for (int i = 0; i < allPictures.size(); i++) {
                PictureInfo picture = allPictures.get(i);
                String uri = groupDirectory+"/"+picture.getLocalUri();
                String author = picture.getUserName();
                if (picturesMap.containsKey(picture.getUserName())) {
                    if(!picturesMap.get(picture.getUserName()).contains(uri)) {
                        picturesMap.get(picture.getUserName()).add(uri);
                    }
                } else {
                    ArrayList<String> arrList = new ArrayList<String>();
                    arrList.add(uri);
                    picturesMap.put(picture.getUserName() ,arrList);
                }
            }
        }
        LinearLayout contents = scrollView.findViewById(R.id.scrolledContents);
        contents.removeAllViews();
        for (Map.Entry<String, ArrayList<String>> entry: picturesMap.entrySet()) {
            View divider = LayoutInflater.from(getApplicationContext()).inflate(R.layout.album_view_divider, null);
            ((TextView)divider.findViewById(R.id.dividerText)).setText(entry.getKey());
            contents.addView(divider);
            ArrayList<String> picUrls = entry.getValue();
            GridView picGrid = (GridView)LayoutInflater.from(PictureAlbumActivity.this).inflate(R.layout.album_view_grid, null);
            final PictureAlbumAdapter picAdapter = new PictureAlbumAdapter(PictureAlbumActivity.this, picUrls);
            picGrid.setAdapter(picAdapter);
            picGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Intent intent = new Intent(PictureAlbumActivity.this, PhotoViewActivity.class);
                    Bundle b = new Bundle();
                    b.putString("path", picAdapter.getItem(position)); //Image path
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });
            contents.addView(picGrid);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.album_activity_menu, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menuSort:
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPref.edit();
                if (sortMode.equals("people") || sortMode.equals("")) {
                    editor.putString("album_sort_mode", "author");
                    mMenu.findItem(R.id.menuSort).setTitle("Sort by people vs landscape");
                } else {
                    editor.putString("album_sort_mode", "people");
                    mMenu.findItem(R.id.menuSort).setTitle("Sort by author");
                }
                editor.commit();
                String sortMode = sharedPref.getString("album_sort_mode", "");
                reloadPicturesIntoView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onStop() {
        super.onStop();
        if (mGroupListener != null) {
            mGroupsReference.removeEventListener(mGroupListener);
        }

        /**if (mPicturesListener != null) {
            mPicturesReference.removeEventListener(mPicturesListener);
        }*/
    }
}
