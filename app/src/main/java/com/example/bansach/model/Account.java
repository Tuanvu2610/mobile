package com.example.bansach.model;

public class Account {
    private String user_id;
    private String Username;
    private String Password;
    private String Role;
    private String Status;

    public Account() {}

    public Account(String user_id, String username, String password, String role, String status) {
        this.user_id = user_id;
        this.Username = username;
        this.Password = password;
        this.Role = role;
        this.Status = status;
    }

    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }

    public String getUsername() { return Username; }
    public void setUsername(String username) { Username = username; }

    public String getPassword() { return Password; }
    public void setPassword(String password) { Password = password; }

    public String getRole() { return Role; }
    public void setRole(String role) { Role = role; }

    public String getStatus() { return Status; }
    public void setStatus(String status) { Status = status; }
}