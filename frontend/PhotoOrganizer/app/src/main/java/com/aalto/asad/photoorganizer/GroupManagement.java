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

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GroupManagement extends AppCompatActivity {

    private static final String TAG = "MCC";
    //TODO: Replace with real value
    private static final String joinGroupUrl = "https://mcc-fall-2017-g18.appspot.com/joinGroup";

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
                inviterToken = result.getContents();
                String[] strs = inviterToken.split("/");
                groupID = strs[0];
                mFirebaseUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String userToken = task.getResult().getToken();

                            OkHttpClient okHttpClient = new OkHttpClient();
                            FormBody.Builder formBuilder = new FormBody.Builder();
                            formBuilder.add("QRToken", inviterToken);
                            formBuilder.add("userToken", userToken);
                            RequestBody formBody = formBuilder.build();

                            //Send the request
                            Request request = new Request.Builder()
                                    .url(joinGroupUrl)
                                    .post(formBody)
                                    .build();

                            okHttpClient.newCall(request).enqueue(new Callback() {

                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    Toast.makeText(GroupManagement.this, "Unable to join group, try again later.", Toast.LENGTH_LONG).show();
                                    Log.i(TAG, "executePost:failure " + e.getMessage());
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    if (response.isSuccessful()) {
                                        String res = response.body().string();
                                        Log.i(TAG, "executePost:ResponseSuccess " + res);
                                        Intent viewGroupIntent = new Intent(getApplicationContext(), ViewGroup.class);
                                        viewGroupIntent.putExtra("Group", groupID);
                                        viewGroupIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                        startActivity(viewGroupIntent);
                                        finish();
                                    } else {
                                        Log.i(TAG, "executePost:ResponseFailure");
                                    }
                                }
                            });
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
