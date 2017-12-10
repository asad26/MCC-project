package com.aalto.asad.photoorganizer;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.net.ConnectivityManager.TYPE_WIFI;

/**
 * Created by Asad on 12/9/2017.
 */

public class DownloadImages {

    private Context mContext;
    private SharedPreferences sharedPref;
    private String groupID;
    private AppDatabase appDatabase;
    private DatabaseReference mPicturesReference;
    private StorageReference mStorageReference;
    private DatabaseReference mUsersReference;

    private String picFull;
    private String picHigh;
    private String picLow;
    private String userName;


    public DownloadImages(Context mContext) {
        this.mContext = mContext;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        appDatabase = AppDatabase.getInstance(mContext);
        mStorageReference = FirebaseStorage.getInstance().getReference();
    }


    public void listenerStorage() {
            groupID = sharedPref.getString("group_id", "");

            mPicturesReference = FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).child("pictures");
            mPicturesReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                    groupID = sharedPref.getString("group_id", "");
                    Log.i("MCC", "downloadImages:Group id " + groupID);
                    if (!groupID.isEmpty()) {
                        PicturesGroup picturesGroup = new PicturesGroup();
                        picturesGroup.setContains_people(dataSnapshot.getValue(PicturesGroup.class).getContains_people());
                        picturesGroup.setPicture(dataSnapshot.getValue(PicturesGroup.class).getPicture());
                        picturesGroup.setPicture_640(dataSnapshot.getValue(PicturesGroup.class).getPicture_640());
                        picturesGroup.setPicture_1280(dataSnapshot.getValue(PicturesGroup.class).getPicture_1280());
                        picturesGroup.setUser_id(dataSnapshot.getValue(PicturesGroup.class).getUser_id());

                        String containsPeople = picturesGroup.getContains_people().toString();
                        //Log.i("MCC", "onChildAdded " + picturesGroup.getUser_id());

                        mStorageReference.child(picturesGroup.getPicture_640()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                picLow = uri.toString();       // Low pic
                                Log.i("MCC", "onChildAdded " + picLow);
                            }
                        });

                        mStorageReference.child(picturesGroup.getPicture_1280()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                picHigh = uri.toString();       // High pic
                                Log.i("MCC", "onChildAdded " + picHigh);
                            }
                        });

                        mStorageReference.child(picturesGroup.getPicture()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                picFull = uri.toString();       // Full
                                Log.i("MCC", "onChildAdded " + picFull);
                            }
                        });


                        mUsersReference = FirebaseDatabase.getInstance().getReference().child("users").child(picturesGroup.getUser_id());
                        mUsersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                userName = user.getUserName();
                                Log.i("MCC", "user Name " + userName);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

//                        if (picLow != null && picHigh != null && picFull != null && userName != null) {
//                            Log.i("MCC", "before insertToRoomDatabase function");
//                            insertToRoomDatabase(groupID, containsPeople, picFull, picHigh, picLow, userName);
//                        }

                        Log.i("MCC", "before insertToRoomDatabase function");
                        readData(groupID);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

        //}
    }

    public void insertToRoomDatabase(final String groupID, final String containsPeople, final String picFull, final String picHigh, final String picLow, final String userName) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                PictureInfo pictureInfo = new PictureInfo();
                pictureInfo.setGroupId(groupID);
                pictureInfo.setContainsPeople(containsPeople);
                pictureInfo.setPictureFull(picFull);
                pictureInfo.setPictureHigh(picHigh);
                pictureInfo.setPictureLow(picLow);
                pictureInfo.setUserName(userName);
                appDatabase.pictureInfoDao().insertOnlySingleRecord(pictureInfo);
                Log.d("MCC", "Data added to the database");
                return null;
            }
        }.execute();
    }


    // For reading
    public void readData(final String groupID) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                List<PictureInfo> pictureInfoList = appDatabase.pictureInfoDao().getAll();
                for (int i = 0; i < pictureInfoList.size();i++) {
                    PictureInfo p = pictureInfoList.get(i);
                    Log.i("MCC", "in Read: room database " + p.getPictureFull());
                }
                return null;
            }
        }.execute();
    }
}
