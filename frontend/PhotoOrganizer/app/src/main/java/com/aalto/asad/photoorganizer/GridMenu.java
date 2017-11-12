package com.aalto.asad.photoorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Asad on 11/8/2017.
 */

public class GridMenu extends AppCompatActivity {

    private static final String TAG = "MCC";

    private Button groupManagement;
    private Button signOut;

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_menu);

        groupManagement = (Button) findViewById(R.id.button4);
        signOut = (Button) findViewById(R.id.signOut);

        mFirebaseAuth = FirebaseAuth.getInstance();

        groupManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateGroup.class);
                startActivity(intent);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuth.signOut();
                Log.d(TAG, "signOut:success");
                Intent intent = new Intent(GridMenu.this, MainActivity.class);
                startActivity(intent);

            }
        });
    }
}
