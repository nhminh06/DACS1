package com.cafe.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.DoubleProperty;

public class NhanVien {
    private StringProperty maNV;
    private StringProperty hoTen;
    private StringProperty chucVu;
    private StringProperty sdt;
    private StringProperty email;
    private DoubleProperty luong;

    public NhanVien(String maNV, String hoTen, String chucVu, String sdt, String email, double luong) {
        this.maNV = new SimpleStringProperty(maNV);
        this.hoTen = new SimpleStringProperty(hoTen);
        this.chucVu = new SimpleStringProperty(chucVu);
        this.sdt = new SimpleStringProperty(sdt);
        this.email = new SimpleStringProperty(email);
        this.luong = new SimpleDoubleProperty(luong);
    }

    // Getters for TableView bindings
    public StringProperty maNVProperty() { return maNV; }
    public StringProperty hoTenProperty() { return hoTen; }
    public StringProperty chucVuProperty() { return chucVu; }
    public StringProperty sdtProperty() { return sdt; }
    public StringProperty emailProperty() { return email; }
    public DoubleProperty luongProperty() { return luong; }

    // Getters & Setters (optional)
    public String getMaNV() { return maNV.get(); }
    public void setMaNV(String maNV) { this.maNV.set(maNV); }

    public String getHoTen() { return hoTen.get(); }
    public void setHoTen(String hoTen) { this.hoTen.set(hoTen); }

    public String getChucVu() { return chucVu.get(); }
    public void setChucVu(String chucVu) { this.chucVu.set(chucVu); }

    public String getSdt() { return sdt.get(); }
    public void setSdt(String sdt) { this.sdt.set(sdt); }

    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }

    public double getLuong() { return luong.get(); }
    public void setLuong(double luong) { this.luong.set(luong); }
}
