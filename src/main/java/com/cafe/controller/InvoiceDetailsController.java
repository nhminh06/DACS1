package com.cafe.controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class InvoiceDetailsController {

    @FXML private Label titleLabel;
    @FXML private VBox detailsContainer;
    @FXML private Label discountLabel;
    @FXML private Label totalLabel;
    @FXML private Button closeButton;

    private double totalBeforeDiscount = 0.0;
    private double discountPercent = 0.0;

    public void loadInvoiceDetails(String maDon) {
        if (maDon == null || maDon.trim().isEmpty()) {
            addMessage("Mã hóa đơn không hợp lệ.");
            return;
        }

        detailsContainer.getChildren().clear();
        titleLabel.setText("Chi tiết hóa đơn: " + maDon);
        totalBeforeDiscount = 0;
        discountPercent = 0;

        String ngayLap = null;

        // 1. Lấy ngày lập từ bảng HoaDon
        String sqlHoaDon = "SELECT NgayLap FROM HoaDon WHERE MaDon = ?";
        try (Connection conn = connectDB();
             PreparedStatement stmt = conn.prepareStatement(sqlHoaDon)) {
            stmt.setString(1, maDon);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ngayLap = rs.getString("NgayLap");
            } else {
                addMessage("Không tìm thấy hóa đơn với mã: " + maDon);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            addMessage("Lỗi khi truy vấn hóa đơn: " + e.getMessage());
            return;
        }

        // 2. Lấy danh sách món và khuyến mãi từ bảng thongke
        String sqlThongKe = "SELECT ten_mon, so_luong, don_gia, tong_tien, KhuyenMai FROM thongke WHERE MaDon = ? AND DATE(ngay) = ?";
        try (Connection conn = connectDB();
             PreparedStatement stmt = conn.prepareStatement(sqlThongKe)) {
            stmt.setString(1, maDon);
            stmt.setString(2, ngayLap);
            ResultSet rs = stmt.executeQuery();

            boolean hasItem = false;
            while (rs.next()) {
                hasItem = true;
                String ten = rs.getString("ten_mon");
                int soLuong = rs.getInt("so_luong");
                double donGia = rs.getDouble("don_gia");
                double tongTien = rs.getDouble("tong_tien");
                double khuyenMai = rs.getDouble("KhuyenMai");
                if (discountPercent == 0) {
                    discountPercent = khuyenMai;
                }
                addItem(ten, soLuong, donGia, tongTien);
                totalBeforeDiscount += tongTien;
            }

            if (!hasItem) {
                addMessage("Không có chi tiết món cho hóa đơn này.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            addMessage("Lỗi khi truy vấn thống kê: " + e.getMessage());
            return;
        }

        // 3. Cập nhật hiển thị giảm giá và tổng tiền
        updateTotals();
    }

    private void addItem(String tenMon, int soLuong, double donGia, double tongTien) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        Label tenLabel = new Label(tenMon);
        tenLabel.setPrefWidth(200);

        Label qtyLabel = new Label("x" + soLuong);
        qtyLabel.setPrefWidth(50);

        Label giaLabel = new Label(String.format("%,.0f VNĐ", donGia));
        giaLabel.setPrefWidth(100);

        Label tongLabel = new Label(String.format("%,.0f VNĐ", tongTien));
        tongLabel.setPrefWidth(100);

        row.getChildren().addAll(tenLabel, qtyLabel, giaLabel, tongLabel);
        detailsContainer.getChildren().add(row);
    }

    private void addMessage(String message) {
        Label msgLabel = new Label(message);
        msgLabel.setStyle("-fx-text-fill: red;");
        detailsContainer.getChildren().add(msgLabel);
        if (totalLabel != null) totalLabel.setText("0 VNĐ");
        if (discountLabel != null) discountLabel.setText("0%");
    }

    private void updateTotals() {
        if (discountLabel != null && totalLabel != null) {
            double discountAmount = totalBeforeDiscount * (discountPercent / 100.0);
            double finalTotal = totalBeforeDiscount - discountAmount;
            discountLabel.setText(String.format("%.0f%%", discountPercent));
            totalLabel.setText(String.format("%,.0f VNĐ", finalTotal));
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    private Connection connectDB() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:13306/coffee_shop", "root", "");
    }
}