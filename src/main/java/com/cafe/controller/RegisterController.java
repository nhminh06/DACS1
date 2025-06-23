package com.cafe.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField emailField;
    @FXML private Label messageLabel;

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

        if (registerUser(user, pass, email)) {
            showMessage("Đăng ký thành công! Quay về đăng nhập...", "green");
            goToLogin();
        } else {
            showMessage("Tài khoản đã tồn tại hoặc lỗi hệ thống.");
        }
    }

    private boolean registerUser(String user, String pass, String email) {
        String sql = "INSERT INTO users(username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String hashedPass = hashPassword(pass); // Mã hóa mật khẩu tại đây

            stmt.setString(1, user);
            stmt.setString(2, hashedPass);
            stmt.setString(3, email);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showMessage(String message) {
        showMessage(message, "red");
    }

    private void showMessage(String message, String color) {
        messageLabel.setText(message);
        messageLabel.setTextFill(javafx.scene.paint.Paint.valueOf(color));
        messageLabel.setVisible(true);
    }

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
        Parent root = FXMLLoader.load(getClass().getResource("/com/cafe/view/login.fxml"));
        root.getStylesheets().add(getClass().getResource("/com/cafe/view/Style.css").toExternalForm());
        Stage stage = (Stage) emailField.getScene().getWindow();
        Scene scene = new Scene(root, 800, 437);
        stage.setScene(scene);
        stage.setTitle("Đăng nhập");
        stage.show();
    }
}
