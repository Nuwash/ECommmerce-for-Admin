package com.example.adminecom.models;

public class PurchaseModel {
    private String purchaseId;
    private String productId;
    private String purchaseDate;
    private int year;
    private int month;
    private int day;
    private double purchasePrice;
    private int purchaseQuantity;

    public PurchaseModel() {
    }

    public PurchaseModel(String purchaseId, String productId, String purchaseDate, int year, int month, int day, double purchasePrice, int purchaseQuantity) {
        this.purchaseId = purchaseId;
        this.productId = productId;
        this.purchaseDate = purchaseDate;
        this.year = year;
        this.month = month;
        this.day = day;
        this.purchasePrice = purchasePrice;
        this.purchaseQuantity = purchaseQuantity;
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public int getPurchaseQuantity() {
        return purchaseQuantity;
    }

    public void setPurchaseQuantity(int purchaseQuantity) {
        this.purchaseQuantity = purchaseQuantity;
    }
}
