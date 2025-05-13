package com.cafe.controller;

import com.cafe.model.SanPham;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.*;
import java.util.Locale;

public class OrderController {

    @FXML
    private FlowPane productContainer; // ✅ Sửa từ VBox → FlowPane trong FXML để tạo layout theo hàng

    private Connection connectDB() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:13306/coffee_shop", "root", "");
    }

    @FXML
    public void initialize() {
        loadSanPhamToUI();
        productContainer.setPrefWrapLength(500);
    }

    private void loadSanPhamToUI() {
        productContainer.getChildren().clear();
        String sql = "SELECT ten_san_pham, gia, mo_ta FROM douong";

        try (Connection conn = connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String ten = rs.getString("ten_san_pham");
                double gia = rs.getDouble("gia");
                String moTa = rs.getString("mo_ta");

                VBox productBox = createProductBox(ten, gia, moTa);
                productContainer.getChildren().add(productBox); // ✅ mỗi sản phẩm là một VBox trong FlowPane
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createProductBox(String ten, double gia, String moTa) {
        VBox box = new VBox();
        box.getStyleClass().add("product-box");

        StackPane imagePane = new StackPane();
        imagePane.getStyleClass().add("product-image-placeholder");

        // Xử lý ID CSS từ tên sản phẩm
        String cssId = ten.toLowerCase(Locale.ROOT)
                .replace(" ", "-")
                .replace("đ", "d")
                .replace("ê", "e")
                .replace("ơ", "o")
                .replace("ư", "u")
                .replaceAll("[^a-z0-9\\-]", "");

        imagePane.setId(cssId);

        Label nameLabel = new Label(ten);
        nameLabel.getStyleClass().add("product-name");

        Label priceLabel = new Label(String.format("%,.0f VNĐ", gia));
        priceLabel.getStyleClass().add("product-price");

        Button addButton = new Button("Thêm");
        addButton.getStyleClass().add("add-button");

        box.getChildren().addAll(imagePane, nameLabel, priceLabel, addButton);
        return box;
    }

    @FXML
    public void gotoManagement(ActionEvent event) {
        switchScene(event, "/com/cafe/view/management.fxml");
    }

    @FXML
    public void gotocustomers(ActionEvent event) {
        switchScene(event, "/com/cafe/view/customers.fxml");
    }

    @FXML
    public void gotoreports(ActionEvent event) {
        switchScene(event, "/com/cafe/view/reports.fxml");
    }

    private void switchScene(ActionEvent event, String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/cafe/view/Style.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Cafe Order");
            primaryStage.setWidth(1200);
            primaryStage.setHeight(900);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
