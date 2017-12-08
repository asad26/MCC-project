package com.aalto.asad.photoorganizer;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Group {
    private String owner;
    private String ownerID;
    private String name;
    private long expiry;
    //private List<Member> members;

    public Group() {

    }

    public Group(String owner, String ownerID, String name, long expiry) {
        this.owner = owner;
        this.ownerID = ownerID;
        this.name = name;
        this.expiry = expiry;
        //this.members = new Vector<>();
    }

    public String getOwner() { return this.owner; }

    public String getOwnerID() { return this.ownerID; }

    public String getName() { return this.name; }

    public long getExpiry() { return this.expiry; }

    //public List<Member> getMembers() { return this.members; }

}
