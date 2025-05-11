package com.cafe.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class loginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;


    @FXML
    private void handleLogin() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (authenticate(user, pass)) {
            errorLabel.setVisible(false);
            System.out.println("Đăng nhập thành công!");
            loadMainScreen(); // Chuyển sang giao diện chính
        } else {
            errorLabel.setText("Tài khoản hoặc mật khẩu sai!");
            errorLabel.setVisible(true);
        }
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/taikhoan", "root", "");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean authenticate(String user, String pass) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user);
            stmt.setString(2, pass);
            ResultSet rs = stmt.executeQuery();

            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void loadMainScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cafe/view/Order.fxml"));
            Parent root = loader.load();
            root.getStylesheets().add(getClass().getResource("/com/cafe/view/Style.css").toExternalForm());

            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setWidth(1200);
            stage.setHeight(900);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
