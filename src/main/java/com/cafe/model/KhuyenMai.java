package com.cafe.model;

import javafx.beans.property.*;

import java.time.LocalDate;

public class KhuyenMai {
    private final StringProperty tenMa;
    private final IntegerProperty giaTri;
    private final ObjectProperty<LocalDate> ngayHetHan;
    private double khuyenMai = 0;

    public KhuyenMai(String tenMa, int giaTri, LocalDate ngayHetHan) {
        this.tenMa = new SimpleStringProperty(tenMa);
        this.giaTri = new SimpleIntegerProperty(giaTri);
        this.ngayHetHan = new SimpleObjectProperty<>(ngayHetHan);
    }

    public StringProperty tenMaProperty() {
        return tenMa;
    }

    public IntegerProperty giaTriProperty() {
        return giaTri;
    }

    public ObjectProperty<LocalDate> ngayHetHanProperty() {
        return ngayHetHan;
    }
    public String getTenMa() {
        return tenMa.get();
    }

    public int getGiaTri() {
        return giaTri.get();
    }

    public LocalDate getNgayHetHan() {
        return ngayHetHan.get();
    }

    public void setKhuyenMai(double giaTri) {
        this.khuyenMai = giaTri;
    }

}

