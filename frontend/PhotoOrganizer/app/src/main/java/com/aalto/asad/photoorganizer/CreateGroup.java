package com.aalto.asad.photoorganizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CreateGroup extends AppCompatActivity {

    private static final String TAG = "MCC";
    private static final String createGroupUrl = "/create-group";

    private Button createButton;
    private TextView groupNameText;
    private TextView groupDurationText;
    private ProgressBar createGroupProgressBar;

    private String name;
    private String duration;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    //private DatabaseReference mFirebaseDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group);
        createButton = (Button) findViewById(R.id.buttonCreateGroup);
        groupNameText = (EditText) findViewById(R.id.editGroupName);
        groupDurationText = (EditText) findViewById(R.id.editDuration);
        createGroupProgressBar = (ProgressBar) findViewById(R.id.createProgressBar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        //mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //TODO: Handling of the response received from backend
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = groupNameText.getText().toString();
                duration = groupDurationText.getText().toString();
                if(!android.text.TextUtils.isDigitsOnly(duration)) {
                    Toast.makeText(CreateGroup.this, "Group duration must be numbers only!", Toast.LENGTH_LONG).show();
                    return;
                }
                createGroupProgressBar.setVisibility(View.VISIBLE);
                mFirebaseUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String userToken = task.getResult().getToken();
                            ApiForBackend commHandler = new ApiForBackend();
                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("groupname", name);
                            params.put("username", mFirebaseUser.getDisplayName());
                            params.put("timeToLive", duration);
                            params.put("userToken", userToken);
                            //String res = commHandler.executePost(createGroupUrl, params);
                            //Log.i(TAG, "response " + res);
                        } else {
                            Toast.makeText(CreateGroup.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "userToken:failure " + task.getException().getMessage());
                        }
                    }
                });
            }
        });
    }

    /** Old code
    private void createGroup(){
        data = {"groupname":"myGroup", "username":"TestUser", "timeToLive":10,"userToken": user['idToken']}

        Intent intent = new Intent(CreateGroup.this, ViewGroup.class);
        intent.putExtra("Group", groupID);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();

        createGroupProgressBar.setVisibility(View.VISIBLE);
        String groupID = mFirebaseDatabaseReference.child("groups").push().getKey();
        String userID = mFirebaseUser.getUid();
        Group group = new Group(userID, name, duration);
        UserGroup userGroup = new UserGroup(groupID);
        mFirebaseDatabaseReference.child("groups").child(groupID).setValue(group);
        mFirebaseDatabaseReference.child("groups").child(groupID).child("members").child(userID)
                .child("QR").setValue("12345");
        mFirebaseDatabaseReference.child("groups").child(groupID).child("members").child(userID)
                .child("name").setValue(mFirebaseUser.getDisplayName());
        mFirebaseDatabaseReference.child("userGroups").child(userID).setValue(userGroup);
        Intent intent = new Intent(CreateGroup.this, ViewGroup.class);
        intent.putExtra("Group", groupID);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    } */

}