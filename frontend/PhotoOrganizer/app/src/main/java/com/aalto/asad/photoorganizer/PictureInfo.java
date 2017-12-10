package com.aalto.asad.photoorganizer;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Asad on 12/8/2017.
 */

@Entity(tableName = "picturesInfo")
public class PictureInfo {

    @PrimaryKey(autoGenerate = true)
    private int rowId;

    @ColumnInfo(name = "group_id")
    private String groupId;

    @ColumnInfo(name = "contains_people")
    private String containsPeople;

    @ColumnInfo(name = "picture_full")
    private String pictureFull;

    @ColumnInfo(name = "picture_high")
    private String pictureHigh;

    @ColumnInfo(name = "picture_low")
    private String pictureLow;

    @ColumnInfo(name = "user_name")
    private String userName;

    @ColumnInfo(name = "local_uri")
    private String localUri;

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public void setGroupId (String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public void setContainsPeople(String containsPeople) {
        this.containsPeople = containsPeople;
    }

    public String getContainsPeople() {
        return this.containsPeople;
    }

    public void setPictureFull (String pictureFull) {
        this.pictureFull = pictureFull;
    }

    public String getPictureFull() {
        return this.pictureFull;
    }

    public void setPictureHigh (String pictureHigh) {
        this.pictureHigh = pictureHigh;
    }

    public String getPictureHigh() { return this.pictureHigh; }

    public void setPictureLow (String pictureLow) {
        this.pictureLow = pictureLow;
    }

    public String getPictureLow() {
        return this.pictureLow;
    }

    public void setUserName (String userName) {
        this.userName = userName;
    }

    public String getUserName() { return this.userName; }

    public String getLocalUri() {
        return this.localUri;
    }

    public void setLocalUri (String localUri) {
        this.localUri = localUri;
    }


}
