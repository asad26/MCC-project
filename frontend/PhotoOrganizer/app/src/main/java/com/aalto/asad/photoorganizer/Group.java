package com.aalto.asad.photoorganizer;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Group {
    private String ownerID;
    private String groupName;
    private long expiryTime;
    //private List<Member> members;

    public Group() {

    }

    public Group(String owner, String name, long expiry) {
        this.ownerID = owner;
        this.groupName = name;
        this.expiryTime = expiry;
        //this.members = new Vector<>();
    }

    public String getOwnerID() { return this.ownerID; }

    public String getGroupName() { return this.groupName; }

    public long getExpiryTime() { return  this.expiryTime; }

    //public List<Member> getMembers() { return this.members; }

}
