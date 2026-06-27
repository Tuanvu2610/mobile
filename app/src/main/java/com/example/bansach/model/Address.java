package com.example.bansach.model;

public class Address {
    private String addressId;
    private String accountId;
    private String name;
    private String phone;
    private String detail;
    private boolean defaultAddress;

    public Address() {
    }

    public Address(String addressId, String accountId, String name, String phone, String detail, boolean defaultAddress) {
        this.addressId = addressId;
        this.accountId = accountId;
        this.name = name;
        this.phone = phone;
        this.detail = detail;
        this.defaultAddress = defaultAddress;
    }

    public String getAddressId() { return addressId; }
    public void setAddressId(String addressId) { this.addressId = addressId; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }

    public boolean isDefaultAddress() { return defaultAddress; }
    public void setDefaultAddress(boolean defaultAddress) { this.defaultAddress = defaultAddress; }
}