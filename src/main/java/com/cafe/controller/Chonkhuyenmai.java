package com.cafe.controller;

import com.cafe.model.KhuyenMai;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

public class Chonkhuyenmai {

    @FXML private TableView<KhuyenMai> bangKhuyenMai;
    @FXML private TableColumn<KhuyenMai, String> cotTenKhuyenMai;
    @FXML private TableColumn<KhuyenMai, Integer> cotGiaTri;
    @FXML private TableColumn<KhuyenMai, LocalDate> cotNgayHetHan;
    @FXML private Button btnChon;
    @FXML private Button btnHuy;

    private ObservableList<KhuyenMai> danhSachKhuyenMai = FXCollections.observableArrayList();
    private OrderController orderController;

    public void setOrderController(OrderController orderController) {
        this.orderController = orderController;
    }

    @FXML
    public void initialize() {
        cotTenKhuyenMai.setCellValueFactory(new PropertyValueFactory<>("tenMa"));
        cotGiaTri.setCellValueFactory(new PropertyValueFactory<>("giaTri"));
        cotNgayHetHan.setCellValueFactory(new PropertyValueFactory<>("ngayHetHan"));
        loadDanhSachKhuyenMai();
    }

    private Connection connectDB() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:13306/coffee_shop", "root", "");
    }

    private void loadDanhSachKhuyenMai() {
        String query = "SELECT ten_ma, gia_tri, ngay_het_han FROM khuyen_mai";
        try (Connection conn = connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            danhSachKhuyenMai.clear();
            while (rs.next()) {
                danhSachKhuyenMai.add(new KhuyenMai(
                        rs.getString("ten_ma"),
                        rs.getInt("gia_tri"),
                        rs.getDate("ngay_het_han").toLocalDate()
                ));
            }

            bangKhuyenMai.setItems(danhSachKhuyenMai);
        } catch (SQLException e) {
            showError("Lỗi khi tải khuyến mãi", e);
        }
    }

    @FXML
    private void chonKhuyenMai() {
        KhuyenMai selected = bangKhuyenMai.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Vui lòng chọn một khuyến mãi.");
            return;
        }

        if (orderController != null) {
            // Gọi phương thức HienThiKhuyenMai để cập nhật cả giảm giá và giao diện
            orderController.HienThiKhuyenMai(selected.getTenMa());
        }

        // Đóng cửa sổ khuyến mãi
        Stage stage = (Stage) btnChon.getScene().getWindow();
        stage.close();
    }


    @FXML
    private void huyChon() {
        Stage stage = (Stage) btnHuy.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message, Exception e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, message + "\n" + e.getMessage());
    }
}