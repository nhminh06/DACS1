package com.cafe.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class Doica {

    @FXML
    private TableView<NhanVien> bangNhanVien;
    @FXML
    private TableColumn<NhanVien, String> cotTenNV;
    @FXML
    private TableColumn<NhanVien, String> cotChucVuNV;
    @FXML
    private TableColumn<NhanVien, String> cotSdtNV;

    @FXML
    private TableView<NhanVien> bangCaTruc;
    @FXML
    private TableColumn<NhanVien, String> cotTenCa;
    @FXML
    private TableColumn<NhanVien, String> cotChucVuCa;
    @FXML
    private TableColumn<NhanVien, String> cotCaLam;

    @FXML
    private Button btnDoiCa;
    @FXML
    private Button btnXoa;
    @FXML
    private Button btnLuu;

    private final ObservableList<NhanVien> dsCaTruc = FXCollections.observableArrayList();

    private Connection connectDB() {
        try {
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:13306/coffee_shop", "root", ""
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    public void initialize() {
        cotTenNV.setCellValueFactory(data -> data.getValue().hoTenProperty());
        cotChucVuNV.setCellValueFactory(data -> data.getValue().chucVuProperty());
        cotSdtNV.setCellValueFactory(data -> data.getValue().sdtProperty());

        cotTenCa.setCellValueFactory(data -> data.getValue().hoTenProperty());
        cotChucVuCa.setCellValueFactory(data -> data.getValue().chucVuProperty());
        cotCaLam.setCellValueFactory(data -> data.getValue().caLamProperty());

        bangCaTruc.setItems(dsCaTruc);

        loadNhanVien();

        btnDoiCa.setOnAction(e -> doDoiCa());
        btnXoa.setOnAction(e -> xoaNhanVienCaTruc());
        btnLuu.setOnAction(e -> luuCaTruc());
    }

    private void loadNhanVien() {
        ObservableList<NhanVien> dsNhanVien = FXCollections.observableArrayList();
        String sql = "SELECT HoTen, ChucVu, SDT FROM nhanvien";

        try (Connection conn = connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                dsNhanVien.add(new NhanVien(
                        rs.getString("HoTen"),
                        rs.getString("ChucVu"),
                        rs.getString("SDT")
                ));
            }

            bangNhanVien.setItems(dsNhanVien);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void doDoiCa() {
        NhanVien nvChon = bangNhanVien.getSelectionModel().getSelectedItem();
        if (nvChon == null) {
            showAlert("Vui lòng chọn một nhân viên để đổi ca.");
            return;
        }

        for (NhanVien nv : dsCaTruc) {
            if (nv.getHoTen().equals(nvChon.getHoTen())) {
                showAlert("Nhân viên này đã có trong ca trực.");
                return;
            }
        }


        nvChon.setCaLam("Chưa phân");
        dsCaTruc.add(nvChon);
    }

    private void xoaNhanVienCaTruc() {
        NhanVien nvChon = bangCaTruc.getSelectionModel().getSelectedItem();
        if (nvChon == null) {
            showAlert("Vui lòng chọn nhân viên để xóa khỏi ca trực.");
            return;
        }

        dsCaTruc.remove(nvChon);
    }

    private void luuCaTruc() {
        if (dsCaTruc.size() < 5) {
            showAlert("Phải có đủ ít nhất 5 nhân viên.");
            return;
        }

        Set<String> chucVuSet = new HashSet<>();
        for (NhanVien nv : dsCaTruc) {
            chucVuSet.add(nv.getChucVu());
        }

        if (chucVuSet.size() < 5) {
            showAlert("Ca trực phải bao gồm đủ 5 chức vụ khác nhau.");
            return;
        }


        ChoiceDialog<String> dialog = new ChoiceDialog<>("Sáng", "Sáng", "Chiều", "Tối");
        dialog.setTitle("Chọn Ca Làm");
        dialog.setHeaderText("Chọn ca làm cho nhóm nhân viên:");
        dialog.setContentText("Ca:");

        dialog.showAndWait().ifPresent(caDuocChon -> {
            try (Connection conn = connectDB()) {

                String deleteSql = "DELETE FROM catruc";
                Statement deleteStmt = conn.createStatement();
                deleteStmt.executeUpdate(deleteSql);


                String sql = "INSERT INTO catruc (HoTen, ChucVu, CaLam) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);

                for (NhanVien nv : dsCaTruc) {
                    stmt.setString(1, nv.getHoTen());
                    stmt.setString(2, nv.getChucVu());
                    stmt.setString(3, caDuocChon);
                    stmt.addBatch();
                }

                stmt.executeBatch();
                showAlert("Đã lưu ca trực thành công.");
                dsCaTruc.clear();

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Lỗi khi lưu ca trực.");
            }
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public static class NhanVien {
        private final javafx.beans.property.SimpleStringProperty hoTen;
        private final javafx.beans.property.SimpleStringProperty chucVu;
        private final javafx.beans.property.SimpleStringProperty sdt;
        private final javafx.beans.property.SimpleStringProperty caLam;

        public NhanVien(String hoTen, String chucVu, String sdt) {
            this.hoTen = new javafx.beans.property.SimpleStringProperty(hoTen);
            this.chucVu = new javafx.beans.property.SimpleStringProperty(chucVu);
            this.sdt = new javafx.beans.property.SimpleStringProperty(sdt);
            this.caLam = new javafx.beans.property.SimpleStringProperty("");
        }

        public javafx.beans.property.StringProperty hoTenProperty() { return hoTen; }
        public javafx.beans.property.StringProperty chucVuProperty() { return chucVu; }
        public javafx.beans.property.StringProperty sdtProperty() { return sdt; }
        public javafx.beans.property.StringProperty caLamProperty() { return caLam; }

        public String getHoTen() { return hoTen.get(); }
        public String getChucVu() { return chucVu.get(); }
        public void setCaLam(String ca) { this.caLam.set(ca); }
    }
}