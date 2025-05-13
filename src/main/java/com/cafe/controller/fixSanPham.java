package com.cafe.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;

public class fixSanPham {

    @FXML private TextField tenSanPhamSua;
    @FXML private TextField giaSanPhamSua;
    @FXML private TextArea moTaSanPhamSua;
    @FXML private Label thongTinSanPhamSua;
    @FXML private Button btnLuu; // Nếu bạn muốn gán fx:id
    @FXML private Button btnHuy;

    private int idSanPham; // ID của sản phẩm trong CSDL

    /**
     * Dùng phương thức này để truyền thông tin sản phẩm cần sửa từ bên ngoài vào
     */
    public void setThongTinSanPham(int id, String ten, double gia, String moTa) {
        this.idSanPham = id;
        tenSanPhamSua.setText(ten);
        giaSanPhamSua.setText(String.valueOf(gia));
        moTaSanPhamSua.setText(moTa);
        thongTinSanPhamSua.setText("Đang sửa sản phẩm ID: " + id);
    }

    @FXML
    private void handleLuu(ActionEvent event) {
        String ten = tenSanPhamSua.getText().trim();
        String giaText = giaSanPhamSua.getText().trim();
        String moTa = moTaSanPhamSua.getText().trim();

        if (ten.isEmpty() || giaText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Vui lòng nhập đầy đủ tên và giá sản phẩm.");
            return;
        }

        double gia;
        try {
            gia = Double.parseDouble(giaText);
            if (gia < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Giá sản phẩm không hợp lệ.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:13306/coffee_shop", "root", "")) {
            String sql = "UPDATE douong SET ten_san_pham=?, gia=?, mo_ta=? WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, ten);
            stmt.setDouble(2, gia);
            stmt.setString(3, moTa);
            stmt.setInt(4, idSanPham);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Cập nhật sản phẩm thành công.");
                ((Stage) tenSanPhamSua.getScene().getWindow()).close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Không tìm thấy sản phẩm để cập nhật.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi kết nối CSDL hoặc câu lệnh SQL.");
        }
    }

    @FXML
    private void handleHuy(ActionEvent event) {
        ((Stage) tenSanPhamSua.getScene().getWindow()).close();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
