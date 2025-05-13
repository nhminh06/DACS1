package com.cafe.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class addController {

    @FXML
    private TextField tenSanPhamMoi;

    @FXML
    private TextField giaSanPhamMoi;

    @FXML
    private TextArea moTaSanPhamMoi;

    // Kết nối tới CSDL
    private Connection connectDB() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:13306/coffee_shop", "root", "");
    }

    // Hàm xử lý khi nhấn nút "Lưu"
    @FXML
    private void themSanPham(ActionEvent event) {
        String ten = tenSanPhamMoi.getText().trim();
        String giaStr = giaSanPhamMoi.getText().trim();
        String moTa = moTaSanPhamMoi.getText().trim();

        if (ten.isEmpty() || giaStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Vui lòng nhập đầy đủ tên và giá sản phẩm.");
            return;
        }

        double gia;
        try {
            gia = Double.parseDouble(giaStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Giá sản phẩm không hợp lệ.");
            return;
        }

        String insertQuery = "INSERT INTO douong (ten_san_pham, gia, mo_ta) VALUES (?, ?, ?)";

        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setString(1, ten);
            pstmt.setDouble(2, gia);
            pstmt.setString(3, moTa);
            pstmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Thêm sản phẩm thành công!");

            // Xóa trắng các trường sau khi thêm
            tenSanPhamMoi.clear();
            giaSanPhamMoi.clear();
            moTaSanPhamMoi.clear();

            // Đóng cửa sổ (tuỳ chọn)
            Stage stage = (Stage) tenSanPhamMoi.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi khi thêm sản phẩm.");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void huyThemSanPham(ActionEvent event) {
        // Đóng cửa sổ hiện tại
        Stage stage = (Stage) tenSanPhamMoi.getScene().getWindow();
        stage.close();
    }
}
