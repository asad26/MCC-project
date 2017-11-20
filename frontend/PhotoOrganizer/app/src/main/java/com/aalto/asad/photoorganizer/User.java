package com.aalto.asad.photoorganizer;

/**
 * Created by Asad on 11/12/2017.
 */

public class User {

    public String userName;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.userName = username;
        this.email = email;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getEmail() {
        return this.email;
    }
}
