package com.aalto.asad.photoorganizer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.encoder.QRCode;

import java.util.HashMap;

public class GroupManagement extends AppCompatActivity {

    private static final String TAG = "MCC";
    //TODO: Replace with real value
    private static final String joinGroupUrl = "/join-group";

    private Button createGroupButton;
    private Button joinGroupButton;

    private String groupID;
    private String inviterID;
    private String inviterToken;

    private DatabaseReference mFirebaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_management);
        createGroupButton = findViewById(R.id.createGroupButton);
        joinGroupButton = findViewById(R.id.joinGroupButton);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupManagement.this, CreateGroup.class);
                startActivity(intent);
            }
        });
        joinGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(GroupManagement.this, JoinGroup.class);
                //startActivity(intent);

                IntentIntegrator integrator = new IntentIntegrator(GroupManagement.this);
                //integrator.setDesiredBarcodeFormats(integrator.QR_CODE_TYPES);
                integrator.setOrientationLocked(true);
                integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES); // `this` is the current Activity
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                String[] res = result.getContents().split(";");
                groupID = res[0];
                inviterID = res[1];
                inviterToken = res[2];
                mFirebaseUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String userToken = task.getResult().getToken();
                            ApiForBackend commHandler = new ApiForBackend();
                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("groupID", groupID);
                            params.put("inviterID", inviterID);
                            params.put("inviterToken", inviterToken);
                            params.put("userToken", userToken);
                            //String res = commHandler.executePost(joinGroupUrl, params);
                            //Log.i(TAG, "response " + res);
                        } else {
                            Toast.makeText(GroupManagement.this, "Unable to read user information from database.", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "userToken:failure " + task.getException().getMessage());
                        }
                    }
                });
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
