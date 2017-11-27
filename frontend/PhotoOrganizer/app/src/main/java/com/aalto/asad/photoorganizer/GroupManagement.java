package com.aalto.asad.photoorganizer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GroupManagement extends AppCompatActivity {

    private Button createGroupButton;
    private Button joinGroupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_management);
        createGroupButton = findViewById(R.id.createGroupButton);
        joinGroupButton = findViewById(R.id.joinGroupButton);
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
                Intent intent = new Intent(GroupManagement.this, JoinGroup.class);
                startActivity(intent);
            }
        });
    }
}
