package com.cafe.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.regex.Pattern;

public class addNhanVien {

    @FXML private TextField txtHoTen;
    @FXML private TextField txtSoDienThoai;
    @FXML private TextField txtDiaChi;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<String> comboChucVu;
    @FXML private ImageView imgAnhNhanVien;
    @FXML private Button btnChonAnh;
    @FXML private Button btnLuu;
    @FXML private Button btnHuy;

    private File selectedImageFile;

    // Gọi khi nhấn nút chọn ảnh
    @FXML
    private void handleChonAnh(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh nhân viên");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Hình ảnh", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            Image image = new Image(file.toURI().toString());
            imgAnhNhanVien.setImage(image);
        }
    }

    // Gọi khi nhấn nút Lưu
    @FXML
    private void handleLuu(ActionEvent event) {
        String hoTen = txtHoTen.getText();
        String sdt = txtSoDienThoai.getText();
        String diaChi = txtDiaChi.getText();
        String email = txtEmail.getText();
        String chucVu = comboChucVu.getValue();

        if (hoTen.isEmpty() || sdt.isEmpty() || diaChi.isEmpty() || email.isEmpty() || chucVu == null) {
            showAlert(Alert.AlertType.ERROR, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        if (!isValidPhoneNumber(sdt)) {
            showAlert(Alert.AlertType.WARNING, "Số điện thoại không hợp lệ.");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.WARNING, "Email không hợp lệ.");
            return;
        }

        // Giả lập lưu dữ liệu
        System.out.println("Nhân viên mới:");
        System.out.println("Họ tên: " + hoTen);
        System.out.println("SĐT: " + sdt);
        System.out.println("Địa chỉ: " + diaChi);
        System.out.println("Email: " + email);
        System.out.println("Chức vụ: " + chucVu);
        System.out.println("Ảnh: " + (selectedImageFile != null ? selectedImageFile.getAbsolutePath() : "Chưa chọn"));

        showAlert(Alert.AlertType.INFORMATION, "Thêm nhân viên thành công!");

        // Xóa form sau khi lưu
        clearForm();
    }

    @FXML
    private void handleHuy(ActionEvent event) {
        clearForm();
    }

    private void clearForm() {
        txtHoTen.clear();
        txtSoDienThoai.clear();
        txtDiaChi.clear();
        txtEmail.clear();
        comboChucVu.setValue(null);
        imgAnhNhanVien.setImage(null);
        selectedImageFile = null;
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isValidPhoneNumber(String sdt) {
        return sdt.matches("\\d{9,11}");
    }

    private boolean isValidEmail(String email) {
        return Pattern.matches("^\\S+@\\S+\\.\\S+$", email);
    }
}

