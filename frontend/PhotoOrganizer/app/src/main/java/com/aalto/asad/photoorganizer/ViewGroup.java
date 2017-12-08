package com.aalto.asad.photoorganizer;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewGroup extends AppCompatActivity {

    private static final String TAG = "MCC";
    private static final String leaveGroupUrl = "/leaveOrDeleteGroup";

    private TextView groupNameText;
    private TextView groupExpirationText;
    private Button addMemberButton;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mGroupReference;
    private ValueEventListener mGroupListener;
    private String groupID;
    private Group group;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);
        addMemberButton = (Button) findViewById(R.id.addMemberButton);
        groupNameText = (TextView) findViewById(R.id.groupNameText);
        groupExpirationText =(TextView) findViewById(R.id.groupExpirationText);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        groupID = getIntent().getStringExtra("Group");

        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupID != null) {
                    Intent intent = new Intent(ViewGroup.this, AddUserActivity.class);
                    intent.putExtra("Group", groupID);
                    startActivity(intent);
                } else {
                    //This should never happen
                    Toast.makeText(ViewGroup.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        ValueEventListener groupListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                group = dataSnapshot.getValue(Group.class);
                displayGroupInfo(group);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "groupListener:onCancelled");
                Toast.makeText(ViewGroup.this, "Unable to read group information from database.", Toast.LENGTH_LONG).show();
            }
        };
        mGroupListener = groupListener;
        mGroupReference = FirebaseDatabase.getInstance().getReference().child("groups").child(groupID);
        mGroupReference.addValueEventListener(groupListener);
    }

    //TODO: display group member names as well
    private void displayGroupInfo(Group group) {
        groupNameText.setText(group.getGroupName());
        groupExpirationText.setText(String.valueOf(group.getExpiryTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_group_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menuLeaveGroup:
                leaveGroup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //TODO: Check the needed params
    private void leaveGroup() {
        //Get user token from Firebase
        mFirebaseUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    String userToken = task.getResult().getToken();
                    ApiForBackend commHandler = new ApiForBackend();
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("userToken", userToken);
                    //String res = commHandler.executePost(leaveGroupUrl, params);
                    //Log.i(TAG, "response " + res);
                } else {
                    Toast.makeText(ViewGroup.this, "Unable to read user information from database.", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "userToken:failure " + task.getException().getMessage());
                }
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        // Detach the ValueEventListener for group information
        if (mGroupListener != null) {
            mGroupReference.removeEventListener(mGroupListener);
        }
    }
}
