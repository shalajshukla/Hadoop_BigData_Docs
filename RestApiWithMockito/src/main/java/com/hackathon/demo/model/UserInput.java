package com.hackathon.demo.model;

public class UserInput {
    private int vendorNumber;
    private int startPeriodKey;
    private int endPeriodKey;
    private int storeId;
    private String type;

    public int getVendorNumber() {
        return vendorNumber;
    }

    public void setVendorNumber(int vendorNumber) {
        this.vendorNumber = vendorNumber;
    }

    public int getStartPeriodKey() {
        return startPeriodKey;
    }

    public void setStartPeriodKey(int startPeriodKey) {
        this.startPeriodKey = startPeriodKey;
    }

    public int getEndPeriodKey() {
        return endPeriodKey;
    }

    public void setEndPeriodKey(int endPeriodKey) {
        this.endPeriodKey = endPeriodKey;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "UserInput{" +
                "vendorNumber=" + vendorNumber +
                ", startPeriodKey=" + startPeriodKey +
                ", endPeriodKey=" + endPeriodKey +
                ", storeId=" + storeId +
                '}';
    }
}
