package com.example.bansach.model;

import com.google.firebase.database.PropertyName;

public class Book {
    private String Ten_Sach;
    private double Don_gia;
    private String img;
    private double Gia_Ban;
    private String Ma_sach;
    private String NXB;
    private String Nam_XB;
    private String TG;
    private int category_id;




    public Book() {
    }

    public Book(String ten_Sach, double don_gia, String img, double gia_Ban, String ma_sach, String NXB, String nam_XB, String TG, int category_id) {
        Ten_Sach = ten_Sach;
        Don_gia = don_gia;
        this.img = img;
        Gia_Ban = gia_Ban;
        Ma_sach = ma_sach;
        this.NXB = NXB;
        Nam_XB = nam_XB;
        this.TG = TG;
        this.category_id = category_id;
    }

    @PropertyName("Ten_Sach")
    public String getTen_Sach() {
        return Ten_Sach;
    }
    @PropertyName("Don_gia")
    public double getDon_gia() {
        return Don_gia;
    }
    @PropertyName("img")
    public String getImg() {
        return img;
    }
//    @PropertyName("Gia_Ban")
//    public double getGia_Ban() {
//        return Gia_Ban;
//    }
//    @PropertyName("Ma_sach")
//    public String getMa_sach() {
//        return Ma_sach;
//    }
//    @PropertyName("NXB")
//    public String getNXB() {
//        return NXB;
//    }
//    @PropertyName("Nam_XB")
//    public String getNam_XB() {
//        return Nam_XB;
//    }
//    @PropertyName("TG")
//    public String getTG() {
//        return TG;
//    }
//    @PropertyName("category_id")
//    public int getCategory_id() {
//        return category_id;
//    }
}