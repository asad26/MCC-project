package com.aalto.asad.photoorganizer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ViewGroup extends AppCompatActivity {

    private TextView groupNameText;
    private TextView groupExpirationText;
    private Button addMemberButton;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabaseReference;
    private ValueEventListener copyPostListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);
        addMemberButton = (Button) findViewById(R.id.addMemberButton);
        groupNameText = (TextView) findViewById(R.id.groupNameText);
        groupExpirationText =(TextView) findViewById(R.id.groupExpirationText);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewGroup.this, AddUserActivity.class);
                startActivity(intent);
            }
        });

    }

    //TODO: Implement this
    private void fetchGroupInfo() {

        groupNameText.setText("Not implemented");
        groupExpirationText.setText("Not implemented");
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchGroupInfo();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
