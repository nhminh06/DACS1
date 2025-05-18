package com.cafe.controller;

import com.cafe.util.EmailSender;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Random;

public class ForgotPasswordController {

    @FXML private TextField emailField, codeField;
    @FXML private PasswordField newPasswordField;
    @FXML private Label statusLabel;

    @FXML private Button loginButton;

    private String verificationCode;

    @FXML
    public void handleSendCode() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            statusLabel.setText("Vui lòng nhập email.");
            return;
        }

        // Tạo mã xác nhận 6 số ngẫu nhiên
        verificationCode = String.format("%06d", new Random().nextInt(1000000));

        try {
            EmailSender.sendVerificationCode(email, verificationCode);
            statusLabel.setText("Mã xác nhận đã được gửi đến email.");
        } catch (Exception e) {
            statusLabel.setText("Không thể gửi email. Kiểm tra lại địa chỉ.");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleResetPassword() {
        String enteredCode = codeField.getText().trim();
        String newPassword = newPasswordField.getText().trim();
        String email = emailField.getText().trim();

        if (enteredCode.isEmpty() || newPassword.isEmpty()) {
            statusLabel.setText("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        if (!enteredCode.equals(verificationCode)) {
            statusLabel.setText("Sai mã xác nhận!");
            return;
        }

        if (newPassword.length() < 6) {
            statusLabel.setText("Mật khẩu mới phải ít nhất 6 ký tự.");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:13306/coffee_shop", "root", "")) {
                String update = "UPDATE users SET password = ? WHERE email = ?";
                try (PreparedStatement stmt = conn.prepareStatement(update)) {
                    stmt.setString(1, newPassword);
                    stmt.setString(2, email);

                    int rows = stmt.executeUpdate();
                    if (rows > 0) {
                        statusLabel.setText("Mật khẩu đã được đặt lại.");
                        verificationCode = null;
                        codeField.clear();
                        newPasswordField.clear();
                    } else {
                        statusLabel.setText("Email không tồn tại trong hệ thống.");
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            statusLabel.setText("Không tìm thấy driver MySQL.");
            e.printStackTrace();
        } catch (SQLException e) {
            statusLabel.setText("Lỗi kết nối cơ sở dữ liệu.");
            e.printStackTrace();
        }
    }
    @FXML
    private void gotologin() throws IOException {
        // Load giao diện đăng nhập từ file FXML
        Parent root = FXMLLoader.load(getClass().getResource("/com/cafe/view/login.fxml"));

        // Thêm stylesheet vào root
        root.getStylesheets().add(getClass().getResource("/com/cafe/view/Style.css").toExternalForm());

        // Lấy Stage hiện tại từ emailField
        Stage stage = (Stage) emailField.getScene().getWindow();

        // Tạo Scene mới và đặt vào Stage
        Scene scene = new Scene(root, 800, 437);
        stage.setScene(scene);
        stage.setTitle("Đăng nhập");
        stage.show();
    }
}
