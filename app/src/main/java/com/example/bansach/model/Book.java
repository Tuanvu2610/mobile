package com.example.bansach.model;

public class Book {
    private String TenSP;
    private double Don_gia;
    private String img;
    private double Gia_Ban;
    private int MaSP;
    private String NXB;
    private String Nam_XB;
    private String TG;
    private int category_id;
    private int reviewCount;
    private double averageRating;
    public Book() {
    }

    public Book(String tenSP, double don_gia, String img, double gia_Ban, int maSP, String NXB, String nam_XB, String TG, int category_id) {
        TenSP = tenSP;
        Don_gia = don_gia;
        this.img = img;
        Gia_Ban = gia_Ban;
        MaSP = maSP;
        this.NXB = NXB;
        Nam_XB = nam_XB;
        this.TG = TG;
        this.category_id = category_id;
    }

    public String getTenSP() {
        return TenSP;
    }

    public void setTenSP(String tenSP) {
        TenSP = tenSP;
    }

    public double getDon_gia() {
        return Don_gia;
    }

    public void setDon_gia(double don_gia) {
        Don_gia = don_gia;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public double getGia_Ban() {
        return Gia_Ban;
    }

    public void setGia_Ban(double gia_Ban) {
        Gia_Ban = gia_Ban;
    }

    public int getMaSP() {
        return MaSP;
    }

    public void setMaSP(int maSP) {
        MaSP = maSP;
    }

    public String getNXB() {
        return NXB;
    }

    public void setNXB(String NXB) {
        this.NXB = NXB;
    }

    public String getNam_XB() {
        return Nam_XB;
    }

    public void setNam_XB(String nam_XB) {
        Nam_XB = nam_XB;
    }

    public String getTG() {
        return TG;
    }

    public void setTG(String TG) {
        this.TG = TG;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

//    public int getParent_id() {
//        return parent_id;
//    }

}