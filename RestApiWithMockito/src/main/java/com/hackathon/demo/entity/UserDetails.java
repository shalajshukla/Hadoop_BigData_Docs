package com.hackathon.demo.entity;

public class UserDetails {
    private int vendorId;
    private String UserName;

    public UserDetails(int vendorId, String userName) {
        this.vendorId = vendorId;
        UserName = userName;
    }

    public UserDetails(){}
    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}
