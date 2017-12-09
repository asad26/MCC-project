package com.aalto.asad.photoorganizer;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Asad on 12/8/2017.
 */

public class PicturesGroup {

    private String contains_people;
    private String picture;
    private String picture_1280;
    private String picture_640;
    private String user_id;


    public PicturesGroup() {

    }

    public PicturesGroup (String containsPeople, String pictureFull, String pictureHigh, String pictureLow, String userId) {
        this.contains_people = containsPeople;
        this.picture = pictureFull;
        this.picture_1280 = pictureHigh;
        this.picture_640 = pictureLow;
        this.user_id = userId;
    }

//    public void setUserId (String userId) {
//        this.userId = userId;
//    }

    public String getUserId() {
        return this.user_id;
    }

//    public void setContainsPeople(String containsPeople) {
//        this.containsPeople = containsPeople;
//    }

    public String getContainsPeople() {
        return this.contains_people;
    }

//    public void setPictureFull (String pictureFull) {
//        this.pictureFull = pictureFull;
//    }

    public String getPictureFull() {
        return this.picture;
    }

//    public void setPictureHigh (String pictureHigh) {
//        this.pictureHigh = pictureHigh;
//    }

    public String getPictureHigh() { return this.picture_1280; }

//    public void setPictureLow (String pictureLow) {
//        this.pictureLow = pictureLow;
//    }

    public String getPictureLow() {
        return this.picture_640;
    }

}
