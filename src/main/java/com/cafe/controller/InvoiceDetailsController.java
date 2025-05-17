package com.cafe.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InvoiceDetailsController {

    @FXML private Label titleLabel;
    @FXML private VBox detailsContainer;
    @FXML private Button closeButton;

    public void addItem(String tenMon, int soLuong, double donGia, double tongTien) {
        HBox itemRow = new HBox(10);
        itemRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label tenLabel = new Label(tenMon);
        tenLabel.setPrefWidth(200);

        Label qtyLabel = new Label("x" + soLuong);
        qtyLabel.setPrefWidth(50);

        Label giaLabel = new Label(String.format("%,.0f VNĐ", donGia));
        giaLabel.setPrefWidth(100);

        Label tongLabel = new Label(String.format("%,.0f VNĐ", tongTien));
        tongLabel.setPrefWidth(100);

        itemRow.getChildren().addAll(tenLabel, qtyLabel, giaLabel, tongLabel);
        detailsContainer.getChildren().add(itemRow);
    }

    public void loadInvoiceDetails(String maDon) throws SQLException {
        detailsContainer.getChildren().clear();
        titleLabel.setText("Chi tiết hóa đơn: " + maDon);

        // Kiểm tra xem MaDon có tồn tại trong HoaDon
        String queryHoaDon = "SELECT DATE(NgayLap) AS NgayLap FROM HoaDon WHERE MaDon = ?";
        String ngayLap = null;
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(queryHoaDon)) {
            pstmt.setString(1, maDon);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                ngayLap = rs.getString("NgayLap");
            } else {
                addItem("Không tìm thấy hóa đơn với mã: " + maDon, 0, 0, 0);
                return;
            }
        }

        // Lấy chi tiết từ thongke dựa trên MaDon và ngày khớp
        String queryThongKe = "SELECT ten_mon, so_luong, don_gia, tong_tien FROM thongke WHERE MaDon = ? AND DATE(ngay) = ?";
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(queryThongKe)) {
            pstmt.setString(1, maDon);
            pstmt.setString(2, ngayLap);
            ResultSet rs = pstmt.executeQuery();
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                String tenMon = rs.getString("ten_mon");
                int soLuong = rs.getInt("so_luong");
                double donGia = rs.getDouble("don_gia");
                double tongTien = rs.getDouble("tong_tien");
                addItem(tenMon, soLuong, donGia, tongTien);
            }
            if (!hasData) {
                addItem("Không có dữ liệu chi tiết cho hóa đơn này", 0, 0, 0);
            }
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