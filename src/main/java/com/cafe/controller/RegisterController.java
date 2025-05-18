package com.cafe.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField emailField;
    @FXML private Label messageLabel;

    // Kết nối đến cơ sở dữ liệu MySQL
    private Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:13306/coffee_shop", "root", "");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @FXML
    private void handleRegister() {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText();
        String confirm = confirmPasswordField.getText();
        String email = emailField.getText().trim();

        // Kiểm tra hợp lệ
        if (user.isEmpty() || pass.isEmpty() || confirm.isEmpty() || email.isEmpty()) {
            showMessage("Vui lòng điền đầy đủ thông tin.");
            return;
        }

        if (!pass.equals(confirm)) {
            showMessage("Mật khẩu xác nhận không khớp.");
            return;
        }

        if (!email.matches("^\\S+@\\S+\\.\\S+$")) {
            showMessage("Email không hợp lệ.");
            return;
        }

        // Đăng ký người dùng
        if (registerUser(user, pass, email)) {
            showMessage("Đăng ký thành công! Quay về đăng nhập...", "green");
            goToLogin();
        } else {
            showMessage("Tài khoản đã tồn tại hoặc lỗi hệ thống.");
        }
    }

    // Thêm người dùng vào DB
    private boolean registerUser(String user, String pass, String email) {
        String sql = "INSERT INTO users(username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user);
            stmt.setString(2, pass);
            stmt.setString(3, email);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Hiển thị lỗi
    private void showMessage(String message) {
        showMessage(message, "red");
    }

    // Hiển thị thông báo màu
    private void showMessage(String message, String color) {
        messageLabel.setText(message);
        messageLabel.setTextFill(javafx.scene.paint.Paint.valueOf(color));
        messageLabel.setVisible(true);
    }

    // Quay lại giao diện đăng nhập
    private void goToLogin() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/cafe/view/Login.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/cafe/view/Style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Đăng nhập");
            stage.setWidth(800);
            stage.setHeight(437);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
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
