package com.example.bansach.model;

import com.google.firebase.database.PropertyName;

import java.util.List;

public class Order {
    private String customerName;
    private String customerPhone;
    private String orderDate;
    private String orderId;
    private int userId;
    private String address;
    @PropertyName("totalAmount")
    private double totalAmount;
    private String paymentMethod;
    private String status;
    private List<CartItem> listItems;

    public Order() {;
    }

    public Order(String customerName, String customerPhone, String orderDate, String orderId, int userId, String address, double totalAmount, String paymentMethod, String status, List<CartItem> listItems) {
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.orderDate = orderDate;
        this.orderId = orderId;
        this.userId = userId;
        this.address = address;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.listItems = listItems;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    @PropertyName("totalAmount")
    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CartItem> getListItems() {
        return listItems;
    }

    public void setListItems(List<CartItem> listItems) {
        this.listItems = listItems;
    }
}
