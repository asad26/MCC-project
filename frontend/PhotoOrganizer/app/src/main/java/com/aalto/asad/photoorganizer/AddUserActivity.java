package com.aalto.asad.photoorganizer;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class AddUserActivity extends AppCompatActivity {

    private ImageView imageView;

    private String groupID;
    private String userID;
    private String inviteToken;
    private BarcodeEncoder encoder;

    FirebaseUser mFirebaseUser;
    //DatabaseReference mFirebaseReference;
    DatabaseReference tokenReference;
    ValueEventListener tokenListener;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        imageView = findViewById(R.id.qrCodeView);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();
        groupID = getIntent().getStringExtra("Group");
        encoder = new BarcodeEncoder();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        inviteToken = sharedPref.getString("invite_token", null);
        tokenReference = FirebaseDatabase.getInstance().getReference().child("groups")
                .child(groupID).child("tokens");
        if (inviteToken != null) {
            encodeQR(groupID, userID, inviteToken);
        }

        tokenListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                inviteToken = dataSnapshot.toString();
                SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                sharedPrefEditor.putString("invite_token", inviteToken);
                sharedPrefEditor.apply();
                encodeQR(groupID, userID, inviteToken);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        tokenReference.addValueEventListener(tokenListener);

    }

    private void encodeQR(String groupID, String userID, String inviteToken){
        String stringToEncode = groupID+";"+userID+";"+inviteToken;
        try {
            Bitmap bitmap = encoder.encodeBitmap(stringToEncode, BarcodeFormat.QR_CODE, 400, 400);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException wr) {
            return;
        }
    }

    @Override
    public void onStop() {
        tokenReference.removeEventListener(tokenListener);
        super.onStop();
    }
}
