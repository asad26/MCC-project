package com.aalto.asad.photoorganizer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

/**
 * Created by Asad on 11/8/2017.
 */

public class CreateGroup extends AppCompatActivity {

    private Button createButton;
    private TextView groupNameText;
    private TextView groupDurationText;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group);
        createButton = (Button) findViewById(R.id.buttonCreateGroup);
        groupNameText = (EditText) findViewById(R.id.editGroupName);
        groupDurationText = (EditText) findViewById(R.id.editDuration);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = groupNameText.getText().toString();
                String duration = groupDurationText.getText().toString();
                createGroup(name, duration);
            }
        });
    }

    private void createGroup(String name, String duration){
        Toast.makeText(CreateGroup.this, "Not implemented yet", Toast.LENGTH_LONG).show();
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