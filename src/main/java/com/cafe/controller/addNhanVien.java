package com.cafe.controller;

import com.cafe.model.NhanVien;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class addNhanVien {

    @FXML
    private TextField txtHoTen, txtSoDienThoai, txtEmail, txtLuong;

    @FXML
    private ComboBox<String> comboChucVu;

    @FXML
    private Button btnLuu, btnHuy;

    private ObservableList<NhanVien> danhSach;


    private static final String DB_URL = "jdbc:mysql://localhost:13306/coffee_shop";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public void setDanhSach(ObservableList<NhanVien> danhSach) {
        this.danhSach = danhSach;
    }

    @FXML
    public void initialize() {
        btnHuy.setOnAction((ActionEvent e) -> {
            ((Stage) btnHuy.getScene().getWindow()).close();
        });

        btnLuu.setOnAction(this::luuNhanVienMoi);
    }

    private void luuNhanVienMoi(ActionEvent event) {
        String hoTen = txtHoTen.getText();
        String sdt = txtSoDienThoai.getText();
        String email = txtEmail.getText();
        String chucVu = comboChucVu.getValue();

        if (hoTen.isEmpty() || sdt.isEmpty() || email.isEmpty() || chucVu == null) {
            showAlert("Vui lòng điền đầy đủ thông tin.");
            return;
        }

        String luongText = txtLuong.getText();
        if (luongText.isEmpty()) {
            showAlert("Vui lòng nhập lương.");
            return;
        }

        double luong;
        try {
            luong = Double.parseDouble(luongText);
        } catch (NumberFormatException e) {
            showAlert("Lương phải là một số hợp lệ.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

            int index = 1;
            String maNV;
            while (true) {
                maNV = "NV" + index;
                PreparedStatement checkStmt = conn.prepareStatement("SELECT MaNV FROM nhanvien WHERE MaNV = ?");
                checkStmt.setString(1, maNV);
                if (!checkStmt.executeQuery().next()) {
                    break;
                }
                index++;
            }


            NhanVien nv = new NhanVien(maNV, hoTen, chucVu, sdt, email, luong);


            String sql = "INSERT INTO nhanvien (MaNV, HoTen, ChucVu, Sdt, Email, Luong) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, maNV);
            stmt.setString(2, hoTen);
            stmt.setString(3, chucVu);
            stmt.setString(4, sdt);
            stmt.setString(5, email);
            stmt.setDouble(6, luong);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {

                danhSach.add(nv);
                showAlert("Thêm nhân viên thành công.");
                ((Stage) btnLuu.getScene().getWindow()).close();
            } else {
                showAlert("Không thể thêm nhân viên.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi khi lưu vào cơ sở dữ liệu.");
        }
    }

    private void showAlert(String noiDung) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }
}