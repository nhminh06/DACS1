package com.cafe.controller;

import com.cafe.model.SanPham;
import com.cafe.model.KhuyenMai;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class ManagementController {

    @FXML
    private TableView<SanPham> bangSanPham;
    @FXML
    private TableColumn<SanPham, String> cotTen;
    @FXML
    private TableColumn<SanPham, Double> cotGia;
    @FXML
    private TableColumn<SanPham, String> cotMoTa;
    @FXML
    private TextField timKiem;
    private ObservableList<SanPham> danhSachSanPham = FXCollections.observableArrayList();
    private ObservableList<SanPham> danhSachGoc = FXCollections.observableArrayList();



    // === FXML cho phần khuyến mãi ===
    @FXML private TextField tenKhuyenMai;
    @FXML private TextField giaTriKhuyenMai;
    @FXML private DatePicker ngayHetHan;

    @FXML private TableView<KhuyenMai> bangKhuyenMai;
    @FXML private TableColumn<KhuyenMai, String> cotTenKhuyenMai;
    @FXML private TableColumn<KhuyenMai, Integer> cotGiaTri;
    @FXML private TableColumn<KhuyenMai, LocalDate> cotNgayHetHan;

    private ObservableList<KhuyenMai> danhSachKhuyenMai = FXCollections.observableArrayList();

    // Tự động gọi khi giao diện khởi động
    @FXML
    public void initialize() {
        cotTen.setCellValueFactory(new PropertyValueFactory<>("ten"));
        cotGia.setCellValueFactory(new PropertyValueFactory<>("gia"));
        cotMoTa.setCellValueFactory(new PropertyValueFactory<>("moTa"));
        loadSanPhamFromDatabase();

        // Khuyến mãi
        if (cotTenKhuyenMai != null && cotGiaTri != null && cotNgayHetHan != null) {
            cotTenKhuyenMai.setCellValueFactory(new PropertyValueFactory<>("tenMa"));
            cotGiaTri.setCellValueFactory(new PropertyValueFactory<>("giaTri"));
            cotNgayHetHan.setCellValueFactory(new PropertyValueFactory<>("ngayHetHan"));
            loadKhuyenMaiFromDatabase();
        }
        // Tìm kiếm sản phẩm
        timKiem.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                bangSanPham.setItems(danhSachGoc);
            } else {
                String tuKhoa = newValue.toLowerCase();
                ObservableList<SanPham> ketQua = FXCollections.observableArrayList();
                for (SanPham sp : danhSachGoc) {
                    if (sp.getTen().toLowerCase().contains(tuKhoa)) {
                        ketQua.add(sp);
                    }
                }
                bangSanPham.setItems(ketQua);
            }
        });

    }

    private Connection connectDB() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:13306/coffee_shop", "root", "");
    }

    private void loadSanPhamFromDatabase() {
        String query = "SELECT ten_san_pham, gia, mo_ta FROM douong";

        try (Connection conn = connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            danhSachSanPham.clear();
            danhSachGoc.clear();
            while (rs.next()) {
                String ten = rs.getString("ten_san_pham");
                double gia = rs.getDouble("gia");
                String moTa = rs.getString("mo_ta");

                SanPham sp = new SanPham(ten, gia, moTa);
                danhSachSanPham.add(sp);
                danhSachGoc.add(sp);
            }

            bangSanPham.setItems(danhSachSanPham);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // === Tạo khuyến mãi mới ===
    @FXML
    private void taoKhuyenMai(ActionEvent event) {
        String tenMa = tenKhuyenMai.getText().trim();
        String giaTriStr = giaTriKhuyenMai.getText().trim();
        LocalDate ngay = ngayHetHan.getValue();

        if (tenMa.isEmpty() || giaTriStr.isEmpty() || ngay == null) {
            System.out.println("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        int giaTri;
        try {
            giaTri = Integer.parseInt(giaTriStr);
        } catch (NumberFormatException e) {
            System.out.println("Giá trị không hợp lệ.");
            return;
        }

        String insertQuery = "INSERT INTO khuyen_mai (ten_ma, gia_tri, ngay_het_han) VALUES (?, ?, ?)";

        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setString(1, tenMa);
            pstmt.setInt(2, giaTri);
            pstmt.setDate(3, Date.valueOf(ngay));
            pstmt.executeUpdate();

            System.out.println("Tạo mã khuyến mãi thành công!");
            loadKhuyenMaiFromDatabase();

            tenKhuyenMai.clear();
            giaTriKhuyenMai.clear();
            ngayHetHan.setValue(null);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // === Load danh sách khuyến mãi từ DB ===
    private void loadKhuyenMaiFromDatabase() {
        String query = "SELECT ten_ma, gia_tri, ngay_het_han FROM khuyen_mai";

        try (Connection conn = connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            danhSachKhuyenMai.clear();
            while (rs.next()) {
                String tenMa = rs.getString("ten_ma");
                int giaTri = rs.getInt("gia_tri");
                LocalDate ngay = rs.getDate("ngay_het_han").toLocalDate();

                danhSachKhuyenMai.add(new KhuyenMai(tenMa, giaTri, ngay));
            }

            bangKhuyenMai.setItems(danhSachKhuyenMai);

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
    @FXML
    private void taiLai(ActionEvent event) {
        // Làm mới bảng sản phẩm
        loadSanPhamFromDatabase();

        // Làm mới bảng khuyến mãi
        loadKhuyenMaiFromDatabase();

        // Xóa các trường nhập liệu
        tenKhuyenMai.clear();
        giaTriKhuyenMai.clear();
        ngayHetHan.setValue(null);
        timKiem.clear();
    }

    // === Xóa khuyến mãi ===
    @FXML
    private void xoaKhuyenMai(ActionEvent event) {
        KhuyenMai khuyenMaiDuocChon = bangKhuyenMai.getSelectionModel().getSelectedItem();

        if (khuyenMaiDuocChon == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Chú ý");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng chọn mã khuyến mãi cần xóa!");
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Bạn có chắc chắn muốn xóa mã khuyến mãi này?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            String tenMa = khuyenMaiDuocChon.getTenMa();
            String deleteQuery = "DELETE FROM khuyen_mai WHERE ten_ma = ?";

            try (Connection conn = connectDB();
                 PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {

                pstmt.setString(1, tenMa);
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Đã xóa mã khuyến mãi thành công.");
                    loadKhuyenMaiFromDatabase();
                } else {
                    System.out.println("Không tìm thấy mã khuyến mãi cần xóa.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
