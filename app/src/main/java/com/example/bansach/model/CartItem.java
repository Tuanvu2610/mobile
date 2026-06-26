package com.example.bansach.model;

public class CartItem {
    private int maSP;
    private String tenSP;
    private double gia_Ban;
    private String img;
    private int soLuong;

    public CartItem() {}

    public CartItem(int maSP, String tenSP, double gia_Ban, String img, int soLuong) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.gia_Ban = gia_Ban;
        this.img = img;
        this.soLuong = soLuong;
    }

    public int getMaSP() {
        return maSP;
    }

    public void setMaSP(int maSP) {
        this.maSP = maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    public double getGia_Ban() {
        return gia_Ban;
    }

    public void setGia_Ban(double gia_Ban) {
        this.gia_Ban = gia_Ban;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }
}
