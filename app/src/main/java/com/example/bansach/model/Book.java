package com.example.bansach.model;

public class Book {
    private String title;
    private double price;
    private String imageUrl;

    public Book(String title, double price, String imageUrl) {
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Book() {
    }

    public String getTitle() { return title; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
}