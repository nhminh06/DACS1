package com.cafe.model;

public class SanPham {
    private String ten;
    private double gia;
    private String moTa;

    public SanPham(String ten, double gia, String moTa) {
        this.ten = ten;
        this.gia = gia;
        this.moTa = moTa;
    }

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    public double getGia() { return gia; }
    public void setGia(double gia) { this.gia = gia; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}
