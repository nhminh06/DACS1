package com.cafe.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.*;

public class FixSanPham {

    @FXML private TextField tenSanPhamSua;
    @FXML private TextField giaSanPhamSua;
    @FXML private TextArea moTaSanPhamSua;
    @FXML private ImageView anhSanPhamSua; // Có thể giữ lại hoặc xóa nếu không cần hiển thị
    @FXML private Label thongTinSanPhamSua;

    private int sanPhamId;
    private String hinhAnhPath = "";

    public void setThongTinSanPham(int id, String ten, double gia, String moTa, String hinhAnh) {
        this.sanPhamId = id;
        tenSanPhamSua.setText(ten);
        giaSanPhamSua.setText(String.valueOf(gia));
        moTaSanPhamSua.setText(moTa);
        hinhAnhPath = hinhAnh != null ? hinhAnh : ""; // Gán giá trị từ CSDL
        thongTinSanPhamSua.setText("Link ảnh hiện tại: " + (hinhAnh != null ? hinhAnh : "Không có"));
    }

    @FXML
    private void handleLuu(ActionEvent event) {
        String tenMoi = tenSanPhamSua.getText() != null ? tenSanPhamSua.getText().trim() : "";
        String giaStr = giaSanPhamSua.getText() != null ? giaSanPhamSua.getText().trim() : "";
        String moTaMoi = moTaSanPhamSua.getText() != null ? moTaSanPhamSua.getText().trim() : "";

        if (tenMoi.isEmpty() || giaStr.isEmpty() || moTaMoi.isEmpty()) {
            hienCanhBao("Vui lòng nhập đầy đủ thông tin (tên, giá, mô tả).");
            return;
        }

        if (hinhAnhPath == null || hinhAnhPath.isEmpty()) {
            hienCanhBao("Vui lòng chọn một ảnh sản phẩm.");
            return;
        }

        File file = new File(hinhAnhPath);
        if (!file.exists()) {
            hienCanhBao("Ảnh sản phẩm không tồn tại. Vui lòng chọn lại.");
            return;
        }

        double giaMoi;
        try {
            giaMoi = Double.parseDouble(giaStr);
            if (giaMoi <= 0) {
                hienCanhBao("Giá sản phẩm phải lớn hơn 0.");
                return;
            }
        } catch (NumberFormatException e) {
            hienCanhBao("Giá sản phẩm phải là số hợp lệ.");
            return;
        }

        String query = "UPDATE douong SET ten_san_pham = ?, gia = ?, mo_ta = ?, hinh_anh = ? WHERE id = ?";

        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, tenMoi);
            pstmt.setDouble(2, giaMoi);
            pstmt.setString(3, moTaMoi);
            pstmt.setString(4, hinhAnhPath);
            pstmt.setInt(5, sanPhamId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                hienThongBao("Cập nhật sản phẩm thành công!");
                Stage stage = (Stage) tenSanPhamSua.getScene().getWindow();
                ManagementController managementController = (ManagementController) stage.getUserData();
                if (managementController != null) {
                    managementController.loadSanPhamFromDatabase();
                }
                dongCuaSo();
            } else {
                hienCanhBao("Không thể cập nhật sản phẩm. ID không tồn tại.");
            }

        } catch (SQLException e) {
            hienCanhBao("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHuy(ActionEvent event) {
        dongCuaSo();
    }

    @FXML
    private void chonAnhMoi(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn link ảnh sản phẩm");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(tenSanPhamSua.getScene().getWindow());
        if (file != null) {
            hinhAnhPath = file.getAbsolutePath(); // Chỉ lưu đường dẫn
            thongTinSanPhamSua.setText("Link ảnh đã chọn: " + file.getName());
        }
    }

    private Connection connectDB() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:13306/coffee_shop", "root", "");
    }

    private void hienThongBao(String noiDung) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }

    private void hienCanhBao(String noiDung) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Cảnh báo");
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }

    private void dongCuaSo() {
        Stage stage = (Stage) tenSanPhamSua.getScene().getWindow();
        stage.close();
    }
}