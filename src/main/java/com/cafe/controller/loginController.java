package com.cafe.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class loginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if ("admin".equals(user) && "123".equals(pass)) {
            errorLabel.setVisible(false);
            System.out.println("Đăng nhập thành công!");
            // Chuyển sang màn hình chính
        } else {
            errorLabel.setText("Tài khoản hoặc mật khẩu sai!");
            errorLabel.setVisible(true);
        }
    }
}
