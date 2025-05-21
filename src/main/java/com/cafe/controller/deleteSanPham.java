package com.cafe.controller;

import com.cafe.model.SanPham;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class deleteSanPham {

    @FXML
    private Button xoaButton;

    @FXML
    private Button huyButton;

    private SanPham selectedSanPham;

    @FXML
    private Label tenSanPhamXoa;


    public void setSanPham(SanPham sp) {
        this.selectedSanPham = sp;
        tenSanPhamXoa.setText("Bạn có chắc chắn muốn xóa sản phẩm \"" + sp.getTenSanPham() + "\"?");
    }

    @FXML
    public void handleXoa() {
        if (selectedSanPham == null) {
            showAlert("Không có sản phẩm được chọn.");
            return;
        }

        String sql = "DELETE FROM douong WHERE id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:13306/coffee_shop", "root", "");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, selectedSanPham.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                closeWindow();
            } else {
                showAlert("Không tìm thấy sản phẩm để xóa!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Xảy ra lỗi khi xóa sản phẩm.");
        }
    }


    @FXML
    public void handleHuy() {
        closeWindow();
    }


    private void closeWindow() {
        Stage stage = (Stage) tenSanPhamXoa.getScene().getWindow();
        stage.close();
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
