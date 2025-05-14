package com.cafe.controller;

import com.cafe.model.InvoiceItem;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.sql.*;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.Node;

public class OrderController {
    @FXML
    private VBox invoiceItems;

    @FXML
    private Label totalAmount;

    private Map<String, InvoiceItem> invoiceMap = new HashMap<>();

    private double phanTramGiam = 0;


    @FXML
    private FlowPane productContainer;

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
        String sql = "SELECT ten_san_pham, gia, mo_ta, hinh_anh FROM douong";

        try (Connection conn = connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String ten = rs.getString("ten_san_pham");
                double gia = rs.getDouble("gia");
                String moTa = rs.getString("mo_ta");
                String hinhAnh = rs.getString("hinh_anh");

                VBox productBox = createProductBox(ten, gia, moTa, hinhAnh);
                productContainer.getChildren().add(productBox);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createProductBox(String ten, double gia, String moTa, String hinhAnh) {
        VBox box = new VBox(5);
        box.getStyleClass().add("product-box");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);

        if (hinhAnh != null && !hinhAnh.isEmpty()) {
            try {
                File imgFile = new File(hinhAnh);
                if (imgFile.exists()) {
                    Image img = new Image(imgFile.toURI().toString(), true);
                    imageView.setImage(img);
                } else {
                    System.out.println("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Label nameLabel = new Label(ten);
        nameLabel.getStyleClass().add("product-name");

        Label priceLabel = new Label(String.format("%,.0f VNĐ", gia));
        priceLabel.getStyleClass().add("product-price");

        Button addButton = new Button("Thêm");
        addButton.getStyleClass().add("add-button");
        addButton.setOnAction(e -> addToInvoice(ten, gia));

        box.getChildren().addAll(imageView, nameLabel, priceLabel, addButton);
        return box;


    }
    public void setKhuyenMai(double giamPhanTram) {
        this.phanTramGiam = giamPhanTram;
        updateTotal();
    }
    private void addToInvoice(String ten, double gia) {
        if (invoiceMap.containsKey(ten)) {
            InvoiceItem item = invoiceMap.get(ten);
            item.quantity++;
            item.update();
        } else {
            // Nút trừ nhỏ gọn
            Button minusButton = new Button("-");
            minusButton.getStyleClass().add("minus-button");
            minusButton.setMinWidth(15);
            minusButton.setMaxWidth(15);
            minusButton.setMinHeight(15);
            minusButton.setMaxHeight(15);

            // Tên sản phẩm
            Label tenLabel = new Label(ten);
            tenLabel.setMinWidth(100); // Giảm chiều rộng để tránh tràn
            tenLabel.getStyleClass().add("product-name");

            // Số lượng
            Label qtyLabel = new Label("x1");
            qtyLabel.setMinWidth(30);
            qtyLabel.getStyleClass().add("summary-value");

            // Giá
            Label giaLabel = new Label(String.format("%,.0f VNĐ", gia));
            giaLabel.setMinWidth(80);
            giaLabel.getStyleClass().add("summary-value");

            // Spacer cho cân chỉnh
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // Gộp tên sản phẩm với nút trừ
            HBox nameBox = new HBox(5, minusButton, tenLabel);
            nameBox.setAlignment(Pos.CENTER_LEFT);

            // Gộp toàn bộ dòng
            HBox line = new HBox(10, nameBox, spacer, qtyLabel, giaLabel);
            line.setAlignment(Pos.CENTER_LEFT);

            // Thêm dòng vào UI
            invoiceItems.getChildren().add(line);

            // Tạo và lưu item
            InvoiceItem item = new InvoiceItem(line, qtyLabel, giaLabel, 1, gia);
            invoiceMap.put(ten, item);

            // Xử lý khi nhấn nút trừ
            minusButton.setOnAction(event -> {
                item.quantity--;
                if (item.quantity <= 0) {
                    invoiceItems.getChildren().remove(item.container);
                    invoiceMap.remove(ten);
                } else {
                    item.update();
                }
                updateTotal();
            });
        }
        updateTotal();


    }
    private void updateTotal() {
        double total = 0;
        for (InvoiceItem item : invoiceMap.values()) {
            total += item.quantity * item.unitPrice;
        }

        double tongSauGiam = total * (1 - phanTramGiam / 100);
        totalAmount.setText(String.format("%,.0f VNĐ", tongSauGiam));
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

    @FXML
    public void gotokhuyenmai(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cafe/view/chonkhuyenmai.fxml"));
            Parent root = loader.load();
            com.cafe.controller.Chonkhuyenmai controller = loader.getController();
            controller.setOrderController(this);
            Stage stage = new Stage();
            stage.setTitle("Chọn khuyến mãi");
            stage.setScene(new Scene(root, 600, 400));
            stage.show(); // Thêm dòng này để hiển thị cửa sổ
        } catch (IOException e) {
            e.printStackTrace();
        }
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
