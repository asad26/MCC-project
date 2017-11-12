package com.aalto.asad.photoorganizer;

/**
 * Created by Asad on 11/12/2017.
 */

public class User {

    private String userName;
    private String email;

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
