package com.cafe.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.regex.Pattern;

public class fixNhanVien {

    @FXML private TextField txtHoTen;
    @FXML private TextField txtSoDienThoai;
    @FXML private TextField txtDiaChi;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<String> comboChucVu;
    @FXML private Label lblThongBao;
    @FXML private Button btnLuu;
    @FXML private Button btnHuy;

    private String employeeId; // dùng nếu bạn cần lưu ID để sửa trong DB

    @FXML
    public void initialize() {
        // Thêm dữ liệu chức vụ mẫu
        comboChucVu.getItems().addAll("Quản lý", "Thu ngân", "Phục vụ", "Bếp", "Bảo vệ");

        // Ví dụ: tải dữ liệu nhân viên cũ (có thể gọi từ ngoài qua setter)
        loadEmployeeData("Nguyễn Văn A", "0912345678", "Hà Nội", "a.nguyen@example.com", "Thu ngân");
    }

    public void loadEmployeeData(String hoTen, String sdt, String diaChi, String email, String chucVu) {
        txtHoTen.setText(hoTen);
        txtSoDienThoai.setText(sdt);
        txtDiaChi.setText(diaChi);
        txtEmail.setText(email);
        comboChucVu.setValue(chucVu);
        lblThongBao.setText("Đang sửa thông tin nhân viên: " + hoTen);
    }

    @FXML
    private void handleLuu(ActionEvent event) {
        String hoTen = txtHoTen.getText();
        String sdt = txtSoDienThoai.getText();
        String diaChi = txtDiaChi.getText();
        String email = txtEmail.getText();
        String chucVu = comboChucVu.getValue();

        if (hoTen.isEmpty() || sdt.isEmpty() || diaChi.isEmpty() || email.isEmpty() || chucVu == null) {
            showAlert(Alert.AlertType.WARNING, "Vui lòng điền đầy đủ thông tin.");
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

        // Giả lập cập nhật dữ liệu
        System.out.println("Thông tin nhân viên đã cập nhật:");
        System.out.println("Họ tên: " + hoTen);
        System.out.println("SĐT: " + sdt);
        System.out.println("Địa chỉ: " + diaChi);
        System.out.println("Email: " + email);
        System.out.println("Chức vụ: " + chucVu);

        showAlert(Alert.AlertType.INFORMATION, "Cập nhật thành công!");
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
        lblThongBao.setText("");
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
