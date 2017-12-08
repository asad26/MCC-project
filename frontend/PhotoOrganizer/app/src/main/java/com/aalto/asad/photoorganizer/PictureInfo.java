package com.aalto.asad.photoorganizer;

/**
 * Created by Asad on 12/8/2017.
 */

public class PictureInfo {

    public String containsPeople;
    public String pictureFull;
    public String pictureHigh;
    public String pictureLow;
    public String userId;

    public PictureInfo() {

    }

    public String getContainsPeople() {
        return this.containsPeople;
    }

    public String getPictureFull() {
        return this.pictureFull;
    }

    public String getPictureHigh() { return this.pictureHigh; }

    public String getPictureLow() {
        return this.pictureLow;
    }

    public String getUserId() {
        return this.userId;
    }
}
