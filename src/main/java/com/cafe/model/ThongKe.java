package com.cafe.model;

public class ThongKe {
    private String ngay;
    private int soLuong;
    private double doanhThu;
    private double trungBinh;

    public ThongKe(String ngay, int soLuong, double doanhThu, double trungBinh) {
        this.ngay = ngay;
        this.soLuong = soLuong;
        this.doanhThu = doanhThu;
        this.trungBinh = trungBinh;
    }


    public String getNgay() {
        return ngay;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public double getDoanhThu() {
        return doanhThu;
    }

    public double getTrungBinh() {
        return trungBinh;
    }
}
