package com.example.bansach.model;

public class User {
    private String account_id;//đây là user_id của class User
    private String email;
    private String fullName;
    private String birthDay;
    private String sdt;

    public User() {
    }

    public User(String account_id, String email, String fullName, String birthDay, String sdt) {
        this.account_id = account_id;
        this.email = email;
        this.fullName = fullName;
        this.birthDay = birthDay;
        this.sdt = sdt;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }
}
