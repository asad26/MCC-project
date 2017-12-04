package com.aalto.asad.photoorganizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

/**
 * Created by Asad on 11/8/2017.
 */

public class CreateGroup extends AppCompatActivity {

    private Button createButton;
    private TextView groupNameText;
    private TextView groupDurationText;
    private ProgressBar createGroupProgressBar;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;

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
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String name = groupNameText.getText().toString();
                    String durationText = groupDurationText.getText().toString();
                    long duration = Long.parseLong(durationText);
                    createGroup(name, duration);
                } catch (NumberFormatException e) {
                    Toast.makeText(CreateGroup.this, "Expiry time must be a number!", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
    }

    //TODO: Replace with real API call
    private void createGroup(String name, long duration){
        //Toast.makeText(CreateGroup.this, "Not implemented yet", Toast.LENGTH_LONG).show();
        createGroupProgressBar.setVisibility(View.VISIBLE);
        String groupID = mFirebaseDatabaseReference.child("groups").push().getKey();
        String userID = mFirebaseUser.getUid();
        Group group = new Group(userID, name, duration);
        UserGroup userGroup = new UserGroup(groupID);
        mFirebaseDatabaseReference.child("groups").child(groupID).setValue(group);
        mFirebaseDatabaseReference.child("groups").child(groupID).child("members").child(userID)
                .child("token").setValue("12345");
        mFirebaseDatabaseReference.child("userGroups").child(userID).setValue(userGroup);
        Intent intent = new Intent(CreateGroup.this, ViewGroup.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

}

//Fetch the user token
/*mFirebaseUser.getToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            userToken = task.getResult().getToken();
                            // Send token to your backend via HTTPS
                            // ...
                        } else {
                            // Handle error -> task.getException();
                        }
                    }
                }); */