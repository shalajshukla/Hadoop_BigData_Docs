package com.hackathon.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WasteData {
    private Integer periodKey;
    private Integer storeId;
    private Integer upc;
    private String claimReasonName;
    private Integer itemQuantityClaim;
    private Integer whseProdVendorNumber;
    private String whseProdVendorName;
    private Double itemCost;
    private Double extnCostDollar;
    private Integer prodId;
    private String prodCatName;
    private String state;
    private String city;
    private Integer zip;
    private String county;
    private String district;

    public Integer getPeriodKey() {
        return periodKey;
    }

    public void setPeriodKey(Integer periodKey) {
        this.periodKey = periodKey;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getUpc() {
        return upc;
    }

    public void setUpc(Integer upc) {
        this.upc = upc;
    }

    public String getClaimReasonName() {
        return claimReasonName;
    }

    public void setClaimReasonName(String claimReasonName) {
        this.claimReasonName = claimReasonName;
    }

    public Integer getItemQuantityClaim() {
        return itemQuantityClaim;
    }

    public void setItemQuantityClaim(Integer itemQuantityClaim) {
        this.itemQuantityClaim = itemQuantityClaim;
    }

    public Integer getWhseProdVendorNumber() {
        return whseProdVendorNumber;
    }

    public void setWhseProdVendorNumber(Integer whseProdVendorNumber) {
        this.whseProdVendorNumber = whseProdVendorNumber;
    }

    public String getWhseProdVendorName() {
        return whseProdVendorName;
    }

    public void setWhseProdVendorName(String whseProdVendorName) {
        this.whseProdVendorName = whseProdVendorName;
    }

    public Double getItemCost() {
        return itemCost;
    }

    public void setItemCost(Double itemCost) {
        this.itemCost = itemCost;
    }

    public Double getExtnCostDollar() {
        return extnCostDollar;
    }

    public void setExtnCostDollar(Double extnCostDollar) {
        this.extnCostDollar = extnCostDollar;
    }

    public Integer getProdId() {
        return prodId;
    }

    public void setProdId(Integer prodId) {
        this.prodId = prodId;
    }

    public String getProdCatName() {
        return prodCatName;
    }

    public void setProdCatName(String prodCatName) {
        this.prodCatName = prodCatName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getZip() {
        return zip;
    }

    public void setZip(Integer zip) {
        this.zip = zip;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
