package com.cafe.controller;

import com.cafe.model.SanPham;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class ManagementController {

    @FXML
    private TableView<SanPham> bangSanPham;
    @FXML
    private TableColumn<SanPham, String> cotTen;
    @FXML
    private TableColumn<SanPham, Double> cotGia;
    @FXML
    private TableColumn<SanPham, String> cotMoTa;

    private ObservableList<SanPham> danhSachSanPham = FXCollections.observableArrayList();

    // Tự động gọi khi giao diện khởi động
    @FXML
    public void initialize() {
        cotTen.setCellValueFactory(new PropertyValueFactory<>("ten"));
        cotGia.setCellValueFactory(new PropertyValueFactory<>("gia"));
        cotMoTa.setCellValueFactory(new PropertyValueFactory<>("moTa"));
        loadSanPhamFromDatabase();
    }

    private Connection connectDB() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/taikhoan", "root", "");
    }

    private void loadSanPhamFromDatabase() {
        String query = "SELECT ten_san_pham, gia, mo_ta FROM douong";

        try (Connection conn = connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            danhSachSanPham.clear();
            while (rs.next()) {
                String ten = rs.getString("ten_san_pham");
                double gia = rs.getDouble("gia");
                String moTa = rs.getString("mo_ta");

                danhSachSanPham.add(new SanPham(ten, gia, moTa));
            }

            bangSanPham.setItems(danhSachSanPham);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void gotoorder(ActionEvent event) {
        chuyenManHinh(event, "/com/cafe/view/order.fxml", "Cafe Order");
    }

    @FXML
    public void gotocustomers(ActionEvent event) {
        chuyenManHinh(event, "/com/cafe/view/customers.fxml", "Cafe Order");
    }

    @FXML
    public void gotoreports(ActionEvent event) {
        chuyenManHinh(event, "/com/cafe/view/reports.fxml", "Cafe Order");
    }

    @FXML
    public void gotoadd(ActionEvent event) {
        moManHinhMoi("/com/cafe/view/add.fxml", "Cafe Order", 700, 400);
    }

    @FXML
    public void gotofix(ActionEvent event) {
        moManHinhMoi("/com/cafe/view/fix.fxml", "Cafe Order", 700, 400);
    }

    @FXML
    public void gotodelete(ActionEvent event) {
        moManHinhMoi("/com/cafe/view/delete.fxml", "Cafe Order", 700, 400);
    }

    private void chuyenManHinh(ActionEvent event, String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/cafe/view/Style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setWidth(1200);
            stage.setHeight(900);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void moManHinhMoi(String fxmlPath, String title, int width, int height) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage newStage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/cafe/view/Style.css").toExternalForm());
            newStage.setScene(scene);
            newStage.setTitle(title);
            newStage.setWidth(width);
            newStage.setHeight(height);
            newStage.setResizable(false);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
