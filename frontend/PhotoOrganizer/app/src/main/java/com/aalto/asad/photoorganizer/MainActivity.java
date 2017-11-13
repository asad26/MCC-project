package com.aalto.asad.photoorganizer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MCC";

    private Button regButton;
    private Button signInButton;
    private EditText userName;
    private EditText userEmail;
    private EditText userPassword;
    private TextView textName;
    private TextView textAlreadyReg;
    private ProgressBar progressBar;

    //Initialize Firebase parameters
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Buttons
        regButton = (Button) findViewById(R.id.regbutton);
        signInButton = (Button) findViewById(R.id.signInButton);

        // Text Views
        textName = (TextView) findViewById(R.id.textName);
        textAlreadyReg = (TextView) findViewById(R.id.textAlreadyRegistered);

        // Edit Texts
        userName = (EditText) findViewById(R.id.editName);
        userEmail = (EditText) findViewById(R.id.editEmail);
        userPassword = (EditText) findViewById(R.id.editPassword);

        // Progress Bar
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = userName.getText().toString();
                String email = userEmail.getText().toString();
                String pass = userPassword.getText().toString();
                if (regButton.getText().equals("REGISTER")) {
                    createNewAccount(name, email, pass);
                } else {
                    signIn(email, pass);
                }
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signInButton.getText().equals("Sign in")) {
                    textName.setVisibility(View.GONE);
                    userName.setVisibility(View.GONE);
                    regButton.setText(R.string.sign_in_new);
                    textAlreadyReg.setText(R.string.for_sign_up);
                    signInButton.setText(R.string.register_new);
                }
                else {
                    textName.setVisibility(View.VISIBLE);
                    userName.setVisibility(View.VISIBLE);
                    regButton.setText(R.string.register);
                    textAlreadyReg.setText(R.string.already_registered);
                    signInButton.setText(R.string.sign_in);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            Intent intent = new Intent(MainActivity.this, GridMenu.class);
            startActivity(intent);
            finish();
        }
    }

    private void createNewAccount(final String uName, String uEmail, String uPassword) {
        Log.d(TAG, "CreateNewAccount: " + uEmail);
        boolean valid = true;

        if (TextUtils.isEmpty(uName)) {
            userName.setError("Required.");
            valid = false;
        }

        if (TextUtils.isEmpty(uEmail)) {
            userEmail.setError("Required.");
            valid = false;
        }

        if (TextUtils.isEmpty(uPassword)) {
            userPassword.setError("Required.");
            valid = false;
        }

        if (!valid) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mFirebaseAuth.createUserWithEmailAndPassword(uEmail, uPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(MainActivity.this, "Registration successful", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            writeNewUserToDatabase(firebaseUser.getUid(), uName, firebaseUser.getEmail());
                            Intent intent = new Intent(MainActivity.this, GridMenu.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d(TAG, "createUserWithEmail:failure");
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void signIn(String uEmail, String uPassword) {
        Log.d(TAG, "signIn: " + uEmail);
        boolean valid = true;

        if (TextUtils.isEmpty(uEmail)) {
            userEmail.setError("Required.");
            valid = false;
        }

        if (TextUtils.isEmpty(uPassword)) {
            userPassword.setError("Required.");
            valid = false;
        }

        if (!valid) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mFirebaseAuth.signInWithEmailAndPassword(uEmail, uPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(MainActivity.this, GridMenu.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d(TAG, "signInWithEmail:failure");
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void writeNewUserToDatabase(String userId, String name, String email) {
        User user = new User(name, email);
        mFirebaseDatabaseReference.child("users").child(userId).setValue(user);
    }
}
