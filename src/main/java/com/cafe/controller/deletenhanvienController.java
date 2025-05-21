package com.cafe.controller;

import com.cafe.model.NhanVien;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class deletenhanvienController {

    @FXML
    private Label tennhavien;

    private NhanVien selectedNhanVien;

    public void setNhanVien(NhanVien nv) {
        this.selectedNhanVien = nv;
        tennhavien.setText( nv.getHoTen() );
    }

    @FXML
    private void handleXoa() {
        if (selectedNhanVien == null) {
            showAlert("Không có nhân viên được chọn.");
            return;
        }

        String sql = "DELETE FROM nhanvien WHERE MaNV = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:13306/coffee_shop", "root", "");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, selectedNhanVien.getMaNV());
            stmt.executeUpdate();
            closeWindow();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Xảy ra lỗi khi xóa nhân viên!");
        }
    }

    @FXML
    private void handleHuy() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) tennhavien.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
