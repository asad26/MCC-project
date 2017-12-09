package com.aalto.asad.photoorganizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Asad on 12/9/2017.
 */

public class DownloadImages {

    private Context mContext;
    private SharedPreferences sharedPref;
    private String groupID;
    private AppDatabase appDatabase;

    private DatabaseReference mPicturesReference;

    public DownloadImages(Context mContext) {
        this.mContext = mContext;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        //groupID = sharedPref.getString("group_id", "");
        appDatabase = AppDatabase.getInstance(mContext);
    }

    public void listenerStorage() {
        groupID = sharedPref.getString("group_id", "");
        if (groupID.isEmpty()) {
            Log.i("MCC", "no group id");
        } else {

            Log.i("MCC", "group id in download" + groupID);
            mPicturesReference = FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).child("pictures");
            mPicturesReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot child : dataSnapshot.getChildren()) {
                        PicturesGroup picturesGroup = child.getValue(PicturesGroup.class);
                        Log.i("MCC", "Author: " + picturesGroup.getUserId());
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


//            mPicturesReference = FirebaseDatabase.getInstance().getReference().child("groups/" + groupID + "/pictures");
//            Log.i("MCC", "group id in download" + mPicturesReference.toString());
//            mPicturesReference.addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
//                    PicturesGroup picturesGroup = dataSnapshot.getValue(PicturesGroup.class);
//                    Log.i("MCC", "Author: " + picturesGroup.getUserId());
//                    Log.i("MCC", "Author: " + picturesGroup.getContainsPeople());
//                    Log.i("MCC", "Author: " + prevChildKey);
//                }
//
//                @Override
//                public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}
//
//                @Override
//                public void onChildRemoved(DataSnapshot dataSnapshot) {}
//
//                @Override
//                public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {}
//            });
        }


    }
}
