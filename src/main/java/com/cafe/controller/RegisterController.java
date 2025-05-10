package com.cafe.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField emailField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
            showMessage("Vui lòng điền đầy đủ thông tin.", true);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showMessage("Mật khẩu không khớp.", true);
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showMessage("Email không hợp lệ.", true);
            return;
        }

        // Giả lập đăng ký thành công
        showMessage("Đăng ký thành công!", false);
        clearForm();
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setTextFill(isError ? javafx.scene.paint.Color.RED : javafx.scene.paint.Color.GREEN);
        messageLabel.setVisible(true);
    }

    private void clearForm() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        emailField.clear();
    }
}

