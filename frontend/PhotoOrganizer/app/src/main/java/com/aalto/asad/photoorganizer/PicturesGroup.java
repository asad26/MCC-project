package com.aalto.asad.photoorganizer;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Asad on 12/8/2017.
 */

public class PicturesGroup {

    private Boolean contains_people;
    private String picture;
    private String picture_1280;
    private String picture_640;
    private String user_id;
    private String localUri;


    public PicturesGroup() {

    }

    public Boolean getContains_people() {
        return contains_people;
    }

    public void setContains_people(Boolean contains_people) {
        this.contains_people = contains_people;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPicture_1280() {
        return picture_1280;
    }

    public void setPicture_1280(String picture_1280) {
        this.picture_1280 = picture_1280;
    }

    public String getPicture_640() {
        return picture_640;
    }

    public void setPicture_640(String picture_640) {
        this.picture_640 = picture_640;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getLocalUri() { return this.localUri; }

    public void setLocalUri(String localUri) { this.localUri = localUri; }
}
