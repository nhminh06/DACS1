package com.cafe.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
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

    @FXML
    private ImageView anhSanPhamMoi;

    private String duongDanAnh = "";

    private Connection connectDB() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:13306/coffee_shop", "root", "");
    }

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

        String insertQuery = "INSERT INTO douong (ten_san_pham, gia, mo_ta, hinh_anh) VALUES (?, ?, ?, ?)";

        try (Connection conn = connectDB(); PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setString(1, ten);
            pstmt.setDouble(2, gia);
            pstmt.setString(3, moTa);
            pstmt.setString(4, duongDanAnh);

            pstmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Thêm sản phẩm thành công!");

            tenSanPhamMoi.clear();
            giaSanPhamMoi.clear();
            moTaSanPhamMoi.clear();
            anhSanPhamMoi.setImage(null);
            duongDanAnh = "";

            Stage stage = (Stage) tenSanPhamMoi.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi khi thêm sản phẩm.");
        }
    }

    @FXML
    private void chonAnhMinhHoa(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh minh họa");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Chọn file ảnh", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"
        ));

        File file = fileChooser.showOpenDialog(tenSanPhamMoi.getScene().getWindow());

        if (file != null) {
            duongDanAnh = file.getAbsolutePath(); // đường dẫn để lưu vào DB

            // Dùng toURI().toString() để tạo URL hợp lệ cho Image
            Image image = new Image(file.toURI().toString());
            anhSanPhamMoi.setImage(image);
        }
    }

    @FXML
    private void huyThemSanPham(ActionEvent event) {
        Stage stage = (Stage) tenSanPhamMoi.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
