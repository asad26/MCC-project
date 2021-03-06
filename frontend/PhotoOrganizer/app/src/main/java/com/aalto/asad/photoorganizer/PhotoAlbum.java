package com.aalto.asad.photoorganizer;

/**
 * Created by Asad on 11/26/2017.
 */

public class PhotoAlbum {
    private String name;
    private String albumID;
    private String numOfPhotos;
    private String thumbnail;
    private int cloudImage;
    private String groupID;

    public PhotoAlbum() {
    }

    public PhotoAlbum(String name, String id, String numOfPhotos, String thumbnail, int cloudImage) {
        this.name = name;
        this.albumID = id;
        this.numOfPhotos = numOfPhotos;
        this.thumbnail = thumbnail;
        this.cloudImage = cloudImage;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return albumID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumOfPhotos() {
        return numOfPhotos;
    }

    public void setNumOfPhotos(String numOfPhotos) {
        this.numOfPhotos = numOfPhotos;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getCloudImage() {
        return cloudImage;
    }

    public void setCloudImage(int cloudImage) {
        this.cloudImage = cloudImage;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }
}
