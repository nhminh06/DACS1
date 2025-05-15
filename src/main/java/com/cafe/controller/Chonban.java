package com.cafe.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane; // Thêm import này
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Chonban implements Initializable {
    @FXML
    private FlowPane tableContainer;

    private OrderController orderController;

    private Map<Integer, String> tableStatusMap = new HashMap<>();

    public void setOrderController(OrderController controller) {
        this.orderController = controller;
    }

    private Connection connectDB() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:13306/coffee_shop", "root", "");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadAvailableTables();
    }

    private void loadAvailableTables() {
        tableContainer.getChildren().clear();
        tableStatusMap.clear();

        // Lấy trạng thái của tất cả các bàn từ cơ sở dữ liệu
        String sql = "SELECT id_ban, trang_thai FROM ban WHERE id_ban BETWEEN 1 AND 10";
        try (Connection conn = connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int idBan = rs.getInt("id_ban");
                String trangThai = rs.getString("trang_thai");
                tableStatusMap.put(idBan, trangThai);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Hiển thị tất cả các bàn từ 1 đến 10
        for (int idBan = 1; idBan <= 10; idBan++) {
            String trangThai = tableStatusMap.getOrDefault(idBan, "Trống");
            VBox tableBox = new VBox(5);
            tableBox.getStyleClass().add("o-ban");

            Label banLabel = new Label("Bàn " + idBan);
            banLabel.getStyleClass().add("ban-label");

            Label statusLabel = new Label(trangThai.equals("Trống") ? "Trống" : trangThai);
            statusLabel.getStyleClass().add("trang-thai");
            if (!trangThai.equals("Trống")) {
                statusLabel.getStyleClass().add(trangThai.equals("Đang dùng") ? "trang-thai-occupied" : "trang-thai-reserved");
            }

            if (trangThai.equals("Trống")) {
                Button selectButton = new Button("Chọn");
                selectButton.getStyleClass().add("add-button"); // Sử dụng add-button
                final int finalIdBan = idBan;
                selectButton.setOnAction(e -> {
                    orderController.selectTable(finalIdBan);
                    Stage stage = (Stage) selectButton.getScene().getWindow();
                    stage.close();
                });
                tableBox.getChildren().addAll(banLabel, statusLabel, selectButton);
            } else {
                tableBox.getChildren().addAll(banLabel, statusLabel);
            }

            tableContainer.getChildren().add(tableBox);
        }
    }
}