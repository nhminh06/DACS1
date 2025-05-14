package com.cafe.model;

public class SanPham {
    private int id;
    private String ten;
    private double gia;
    private String moTa;
    private String hinhAnh;
    public SanPham(int id, String ten, double gia, String moTa, String hinhAnh) {
        this.id = id;
        this.ten = ten;
        this.gia = gia;
        this.moTa = moTa;
        this.hinhAnh = hinhAnh;
    }

    public int getId() {
        return id;
    }

    public String getTen() {
        return ten;
    }

    public double getGia() {
        return gia;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public void setGia(double gia) {
        this.gia = gia;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
    public String getTenSanPham() {
        return ten;
    }
    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }
}
