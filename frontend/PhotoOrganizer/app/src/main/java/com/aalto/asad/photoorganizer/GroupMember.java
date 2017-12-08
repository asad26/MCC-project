package com.aalto.asad.photoorganizer;


public class GroupMember {

    private String name;
    private String QR;

    public GroupMember() {}

    public GroupMember(String name, String QR){
        this.name = name;
        this.QR = QR;
    }

    public String getName() {
        return this.name;
    }

    public String getQR() {
        return this.QR;
    }


}
