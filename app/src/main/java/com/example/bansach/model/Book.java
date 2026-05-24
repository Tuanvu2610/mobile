package com.example.bansach.model;

public class Book {
    private String Ten_Sach;
    private double Don_gia;
    private String img;



    public Book() {
    }

    public Book(String ten_Sach, double don_gia, String img) {
        Ten_Sach = ten_Sach;
        Don_gia = don_gia;
        this.img = img;
    }

    public String getTen_Sach() {
        return Ten_Sach;
    }

    public double getDon_gia() {
        return Don_gia;
    }

    public String getImg() {
        return img;
    }
}