package com.aashish.aashishpizzaordering;

public class Pizza {
    private int id;
    private String customerName;
    private String mobileNumber;
    private String size;
    private int toppings;
    private double totalBill;

    public Pizza(int id, String customerName, String mobileNumber, String size, int toppings, double totalBill) {
        this.id = id;
        this.customerName = customerName;
        this.mobileNumber = mobileNumber;
        this.size = size;
        this.toppings = toppings;
        this.totalBill = totalBill;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getToppings() {
        return toppings;
    }

    public void setToppings(int toppings) {
        this.toppings = toppings;
    }

    public double getTotalBill() {
        return totalBill;
    }

    public void setTotalBill(double totalBill) {
        this.totalBill = totalBill;
    }
}
