package com.hackathon.demo.entity;

public class Waste {
    private String claimReasonName;
    private String periodKey;
    private String storeId;
    private String prodId;

    public String getClaimReasonName() {
        return claimReasonName;
    }

    public void setClaimReasonName(String claimReasonName) {
        this.claimReasonName = claimReasonName;
    }

    public String getPeriodKey() {
        return periodKey;
    }

    public void setPeriodKey(String periodKey) {
        this.periodKey = periodKey;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }
}
