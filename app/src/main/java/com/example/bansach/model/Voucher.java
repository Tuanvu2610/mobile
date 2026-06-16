package com.example.bansach.model;

public class Voucher {
    private int idVoucher;
    private String hinhAnh;
    private String tieuDe;
    private  String dieuKien;
    private String maVoucher;

    public Voucher() {}

    public Voucher(int idVoucher, String hinhAnh, String tieuDe, String dieuKien, String maVoucher) {
        this.idVoucher = idVoucher;
        this.hinhAnh = hinhAnh;
        this.tieuDe = tieuDe;
        this.dieuKien = dieuKien;
        this.maVoucher = maVoucher;
    }
    public int getidVoucher() {
        return idVoucher;
    }


    public String getHinhAnh() {
        return hinhAnh;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public String getDieuKien() {
        return dieuKien;
    }

    public String getMaVoucher() {
        return maVoucher;
    }
    public void setidVoucher(int idVoucher) {
        this.idVoucher = idVoucher;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public void setDieuKien(String dieuKien) {
        this.dieuKien = dieuKien;
    }

    public void setMaVoucher(String maVoucher) {
        this.maVoucher = maVoucher;
    }
}
