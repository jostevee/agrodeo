package com.ipb.agrodeo;

public class MarketDataList {
    public String productKey;
    public String productName;
    public Integer productPrice;
    public Integer productWeight;
    public String productLocation;
    public String contactNumber;
    public String imageUrl;

    public MarketDataList(){
    }

    public MarketDataList(String productKey, String productName, Integer productPrice, Integer productWeight,
                          String productLocation, String contactNumber, String imageUrl){
        this.productKey = productKey;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productWeight = productWeight;
        this.productLocation = productLocation;
        this.contactNumber = contactNumber;
        this.imageUrl = imageUrl;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String ProductKey) {
        this.productKey = ProductKey;
    }

    public String getProductLocation() {
        return productLocation;
    }

    public void setProductLocation(String productLocation) {
        this.productLocation = productLocation;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Integer getProductWeight() {
        return productWeight;
    }

    public void setProductWeight(Integer productWeight) {
        this.productWeight = productWeight;
    }

    public void setProductName(String ProductName) {
        this.productName = ProductName;
    }


    public String getProductName() {
        return productName;
    }


    public void setProductPrice(Integer productPrice) {
        this.productPrice = productPrice;
    }


    public Integer getProductPrice() {
        return productPrice;
    }


    public void setImageUrl(String ImageUrl) {
        this.imageUrl = ImageUrl;
    }


    public String getImageUrl() {
        return imageUrl;
    }
}
