package com.example.bansach.model;

public class UserDisplayItem {
    private String id;
    private String fullName;
    private String email;
    private String role;
    private String status;

    public UserDisplayItem(String id, String fullName, String email, String role, String status) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}