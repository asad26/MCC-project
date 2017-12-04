package com.aalto.asad.photoorganizer;

import android.app.Activity;
import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ViewGroup extends AppCompatActivity {

    private static final String TAG = "MCC";

    //private TextView groupInfo;
    //private TextView groupNameLabel;
    private TextView groupNameText;
    //private TextView groupExpirationLabel;
    private TextView groupExpirationText;
    //private TextView membersLabel;
    private Button addMemberButton;
    //private ListView memberList;
    //private ProgressBar progressBar;
    //private List<View> viewGroupViews;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String userID;
    private DatabaseReference mUserGroupsReference;
    private DatabaseReference mGroupReference;
    private ValueEventListener mUserGroupsListener;
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
        userID = mFirebaseUser.getUid();
        mUserGroupsReference = FirebaseDatabase.getInstance().getReference().child("userGroups");
        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupID != null) {
                    Intent intent = new Intent(ViewGroup.this, AddUserActivity.class);
                    intent.putExtra("Group", groupID);
                    startActivity(intent);
                } else {
                    Toast.makeText(ViewGroup.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        ValueEventListener userGroupsListener = new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                if((!dataSnapshot.hasChild(userID)) || dataSnapshot.child(userID) == null) {
                    Intent intent = new Intent(ViewGroup.this, GroupManagement.class);
                    startActivity(intent);
                    finish();
                } else {
                    UserGroup userGroup = dataSnapshot.child(userID).getValue(UserGroup.class);
                    groupID = userGroup.getUserGroup();
                    setGroupListener(groupID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "userGroupListener:onCancelled");
                Toast.makeText(ViewGroup.this, "Unable to read user's group from database.", Toast.LENGTH_LONG).show();
            }
        };
        mUserGroupsListener = userGroupsListener;
        mUserGroupsReference.addListenerForSingleValueEvent(userGroupsListener);
    }

    private void setGroupListener(String groupID) {
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

    //TODO: Replace with real API call
    private void leaveGroup() {
        if (mGroupReference != null) {
            mGroupReference.child("members").child(userID).removeValue();
            mUserGroupsReference.child(userID).removeValue();
            finish();
        }
    }

    @Override
    public void onStop() {

        super.onStop();

        if (mUserGroupsListener != null) {
            mUserGroupsReference.removeEventListener(mUserGroupsListener);
        }

        if (mGroupListener != null) {
            mGroupReference.removeEventListener(mGroupListener);
        }
    }
}
