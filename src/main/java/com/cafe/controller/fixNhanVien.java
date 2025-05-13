package com.cafe.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.regex.Pattern;

public class fixNhanVien {

    @FXML private TextField txtHoTen;
    @FXML private TextField txtSoDienThoai;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<String> comboChucVu;
    @FXML private Label lblThongBao;
    @FXML private Button btnLuu;
    @FXML private Button btnHuy;

    private String maNhanVien; // dùng để định danh nhân viên trong DB

    @FXML
    public void initialize() {
        comboChucVu.getItems().addAll("Quản lý", "Thu ngân", "Phục vụ", "Bếp", "Bảo vệ");
    }

    // Hàm gọi từ ngoài để đổ dữ liệu nhân viên cần sửa
    public void setThongTinNhanVien(String maNV, String hoTen, String sdt, String email, String chucVu) {
        this.maNhanVien = maNV;
        txtHoTen.setText(hoTen);
        txtSoDienThoai.setText(sdt);
        txtEmail.setText(email);
        comboChucVu.setValue(chucVu);
        lblThongBao.setText("Đang sửa: " + hoTen + " (" + maNV + ")");
    }

    @FXML
    private void handleLuu(ActionEvent event) {
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSoDienThoai.getText().trim();
        String email = txtEmail.getText().trim();
        String chucVu = comboChucVu.getValue();

        if (hoTen.isEmpty() || sdt.isEmpty() || email.isEmpty() || chucVu == null) {
            showAlert(Alert.AlertType.WARNING, "Vui lòng nhập đầy đủ thông tin.");
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

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:13306/coffee_shop", "root", "")) {
            String sql = "UPDATE nhanvien SET HoTen=?, ChucVu=?, Sdt=?, Email=? WHERE MaNV=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, hoTen);
            stmt.setString(2, chucVu);
            stmt.setString(3, sdt);
            stmt.setString(4, email);
            stmt.setString(5, maNhanVien);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Cập nhật thành công!");
                ((Stage) btnLuu.getScene().getWindow()).close(); // đóng cửa sổ
            } else {
                showAlert(Alert.AlertType.ERROR, "Không tìm thấy nhân viên để cập nhật.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi khi cập nhật cơ sở dữ liệu.");
        }
    }

    @FXML
    private void handleHuy(ActionEvent event) {
        ((Stage) btnHuy.getScene().getWindow()).close(); // đóng cửa sổ
    }

    private boolean isValidPhoneNumber(String sdt) {
        return sdt.matches("\\d{9,11}");
    }

    private boolean isValidEmail(String email) {
        return Pattern.matches("^\\S+@\\S+\\.\\S+$", email);
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
