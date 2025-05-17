package com.cafe.controller;

import com.cafe.model.InvoiceItem;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class InvoiceController {

    @FXML
    private VBox invoiceItems;

    @FXML
    private Label totalAmount;

    @FXML
    private Label discountAmount;

    @FXML
    private Label dateTimeLabel;

    @FXML
    private Button checkoutButton;

    private Map<String, InvoiceItem> hoaDonItems;
    private boolean isConfirmed = false;

    private OrderController orderController;
    public void setOrderController(OrderController orderController) {
        this.orderController = orderController;
    }

    public void setHoaDonItems(Map<String, InvoiceItem> items) {
        this.hoaDonItems = items;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void loadInvoiceData(String selectedTable, Map<String, InvoiceItem> invoiceMap, double phanTramGiam, String totalText) {
        this.hoaDonItems = invoiceMap;

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        dateTimeLabel.setText("Ngày: " + now.format(formatter));

        Label tableLabel = new Label("Bàn: " + selectedTable);
        tableLabel.getStyleClass().add("ban-label");

        invoiceItems.getChildren().clear();
        invoiceItems.getChildren().add(tableLabel);

        for (Map.Entry<String, InvoiceItem> entry : invoiceMap.entrySet()) {
            String ten = entry.getKey();
            InvoiceItem item = entry.getValue();

            Label tenLabel = new Label(ten);
            tenLabel.setMinWidth(150);
            tenLabel.getStyleClass().add("product-name");

            Label qtyLabel = new Label("x" + item.quantity);
            qtyLabel.setMinWidth(30);
            qtyLabel.getStyleClass().add("summary-value");

            Label giaLabel = new Label(String.format("%,.0f VNĐ", item.quantity * item.unitPrice));
            giaLabel.setMinWidth(80);
            giaLabel.getStyleClass().add("summary-value");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox line = new HBox(10, tenLabel, spacer, qtyLabel, giaLabel);
            line.setAlignment(Pos.CENTER_LEFT);
            invoiceItems.getChildren().add(line);
        }

        discountAmount.setText(String.format("%.0f %%", phanTramGiam));
        totalAmount.setText(totalText);
    }

    @FXML
    public void handleThanhToan() {
        if (hoaDonItems == null || hoaDonItems.isEmpty()) return;

        LocalDate today = LocalDate.now();
        String maDon = generateMaDon(today);

        try (Connection conn = connectDB()) {
            // Lưu thông tin hóa đơn vào bảng HoaDon
            String sqlHoaDon = "INSERT INTO HoaDon (MaDon, NgayLap) VALUES (?, ?)";
            PreparedStatement psHoaDon = conn.prepareStatement(sqlHoaDon);
            psHoaDon.setString(1, maDon);
            psHoaDon.setDate(2, Date.valueOf(today));
            psHoaDon.executeUpdate();

            // Lưu chi tiết vào bảng thongke với MaDon
            for (Map.Entry<String, InvoiceItem> entry : hoaDonItems.entrySet()) {
                String tenMon = entry.getKey();
                InvoiceItem item = entry.getValue();
                int soLuong = item.quantity;
                double donGia = item.unitPrice;
                double tongTien = soLuong * donGia;

                String sqlThongKe = "INSERT INTO thongke (ngay, ten_mon, so_luong, don_gia, tong_tien, MaDon) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement psThongKe = conn.prepareStatement(sqlThongKe);
                psThongKe.setDate(1, Date.valueOf(today));
                psThongKe.setString(2, tenMon);
                psThongKe.setInt(3, soLuong);
                psThongKe.setDouble(4, donGia);
                psThongKe.setDouble(5, tongTien);
                psThongKe.setString(6, maDon);
                psThongKe.executeUpdate();
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Thanh toán thành công và đã lưu vào thống kê!");
            alert.showAndWait();

            isConfirmed = true;
            Stage stage = (Stage) checkoutButton.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khi lưu thống kê: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private String generateMaDon(LocalDate date) {
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyMMdd"));
        int sequence = (int) (Math.random() * 1000); // Số ngẫu nhiên từ 0-999
        return String.format("HD%s%03d", dateStr, sequence);
    }

    private Connection connectDB() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:13306/coffee_shop", "root", "");
    }
}