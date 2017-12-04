package com.aalto.asad.photoorganizer;

/**
 * Created by Asad on 11/26/2017.
 */

public class PhotoAlbum {
    private String name;
    private String numOfPhotos;
    private int thumbnail;

    public PhotoAlbum() {
    }

    public PhotoAlbum(String name, String numOfPhotos, int thumbnail) {
        this.name = name;
        this.numOfPhotos = numOfPhotos;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
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

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
