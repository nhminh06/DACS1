package com.cafe.model;

public class ThongKe {
    private String ngay;       // ngày báo cáo dạng String (yyyy-MM-dd)
    private int soLuong;       // tổng số lượng món bán ra trong ngày
    private double doanhThu;   // tổng doanh thu trong ngày
    private double trungBinh;  // trung bình doanh thu trên mỗi món

    public ThongKe(String ngay, int soLuong, double doanhThu, double trungBinh) {
        this.ngay = ngay;
        this.soLuong = soLuong;
        this.doanhThu = doanhThu;
        this.trungBinh = trungBinh;
    }

    // Getters
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
