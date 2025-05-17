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

    // === FXML cho sản phẩm ===
    @FXML private TableView<SanPham> bangSanPham;
    @FXML private TableColumn<SanPham, String> cotTen;
    @FXML private TableColumn<SanPham, Double> cotGia;
    @FXML private TableColumn<SanPham, String> cotMoTa;
    @FXML private TextField timKiem;

    private ObservableList<SanPham> danhSachSanPham = FXCollections.observableArrayList();
    private ObservableList<SanPham> danhSachGoc = FXCollections.observableArrayList();

    // === FXML cho khuyến mãi ===
    @FXML private TextField tenKhuyenMai;
    @FXML private TextField giaTriKhuyenMai;
    @FXML private DatePicker ngayHetHan;
    @FXML private TableView<KhuyenMai> bangKhuyenMai;
    @FXML private TableColumn<KhuyenMai, String> cotTenKhuyenMai;
    @FXML private TableColumn<KhuyenMai, Integer> cotGiaTri;
    @FXML private TableColumn<KhuyenMai, LocalDate> cotNgayHetHan;

    private ObservableList<KhuyenMai> danhSachKhuyenMai = FXCollections.observableArrayList();

    // === FXML cho ca trực ===
    @FXML private TableView<Doica.NhanVien> bangCaTruc;
    @FXML private TableColumn<Doica.NhanVien, String> cotTenCa;
    @FXML private TableColumn<Doica.NhanVien, String> cotChucVuCa;
    @FXML private TableColumn<Doica.NhanVien, String> cotCaLam;

    private ObservableList<Doica.NhanVien> danhSachCaTruc = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Cấu hình bảng sản phẩm
        if (cotTen != null && cotGia != null && cotMoTa != null) {
            cotTen.setCellValueFactory(new PropertyValueFactory<>("ten"));
            cotGia.setCellValueFactory(new PropertyValueFactory<>("gia"));
            cotMoTa.setCellValueFactory(new PropertyValueFactory<>("moTa"));
            loadSanPhamFromDatabase();
        }

        // Cấu hình bảng khuyến mãi
        if (cotTenKhuyenMai != null && cotGiaTri != null && cotNgayHetHan != null) {
            cotTenKhuyenMai.setCellValueFactory(new PropertyValueFactory<>("tenMa"));
            cotGiaTri.setCellValueFactory(new PropertyValueFactory<>("giaTri"));
            cotNgayHetHan.setCellValueFactory(new PropertyValueFactory<>("ngayHetHan"));
            loadKhuyenMaiFromDatabase();
        }

        // Cấu hình bảng ca trực
        if (cotTenCa != null && cotChucVuCa != null && cotCaLam != null) {
            cotTenCa.setCellValueFactory(data -> data.getValue().hoTenProperty());
            cotChucVuCa.setCellValueFactory(data -> data.getValue().chucVuProperty());
            cotCaLam.setCellValueFactory(data -> data.getValue().caLamProperty());
            loadCaTrucFromDatabase();
        }

        // Tìm kiếm sản phẩm
        if (timKiem != null) {
            timKiem.textProperty().addListener((observable, oldValue, newValue) -> {
                String tuKhoa = newValue == null ? "" : newValue.toLowerCase();
                ObservableList<SanPham> ketQua = FXCollections.observableArrayList();
                for (SanPham sp : danhSachGoc) {
                    if (sp.getTen().toLowerCase().contains(tuKhoa)) {
                        ketQua.add(sp);
                    }
                }
                bangSanPham.setItems(tuKhoa.isEmpty() ? danhSachGoc : ketQua);
            });
        }
    }

    private Connection connectDB() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:13306/coffee_shop", "root", "");
    }

    public void loadSanPhamFromDatabase() {
        String query = "SELECT id, ten_san_pham, gia, mo_ta, hinh_anh FROM douong";
        try (Connection conn = connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            danhSachSanPham.clear();
            danhSachGoc.clear();

            while (rs.next()) {
                SanPham sp = new SanPham(
                        rs.getInt("id"),
                        rs.getString("ten_san_pham"),
                        rs.getDouble("gia"),
                        rs.getString("mo_ta"),
                        rs.getString("hinh_anh")
                );
                danhSachSanPham.add(sp);
                danhSachGoc.add(sp);
            }

            bangSanPham.setItems(danhSachSanPham);
        } catch (SQLException e) {
            showError("Lỗi khi tải dữ liệu sản phẩm", e);
        }
    }

    private void loadKhuyenMaiFromDatabase() {
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
            showError("Lỗi khi tải dữ liệu khuyến mãi", e);
        }
    }

    private void loadCaTrucFromDatabase() {
        String query = "SELECT HoTen, ChucVu, CaLam FROM catruc";
        try (Connection conn = connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            danhSachCaTruc.clear();
            while (rs.next()) {
                Doica.NhanVien nv = new Doica.NhanVien(
                        rs.getString("HoTen"),
                        rs.getString("ChucVu"),
                        "" // SDT không cần thiết, để trống
                );
                nv.setCaLam(rs.getString("CaLam"));
                danhSachCaTruc.add(nv);
            }

            bangCaTruc.setItems(danhSachCaTruc);
        } catch (SQLException e) {
            showError("Lỗi khi tải dữ liệu ca trực", e);
        }
    }

    @FXML
    private void taoKhuyenMai(ActionEvent event) {
        String tenMa = tenKhuyenMai.getText().trim();
        String giaTriStr = giaTriKhuyenMai.getText().trim();
        LocalDate ngay = ngayHetHan.getValue();

        if (tenMa.isEmpty() || giaTriStr.isEmpty() || ngay == null) {
            showAlert(Alert.AlertType.WARNING, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        try {
            int giaTri = Integer.parseInt(giaTriStr);
            String query = "INSERT INTO khuyen_mai (ten_ma, gia_tri, ngay_het_han) VALUES (?, ?, ?)";
            try (Connection conn = connectDB(); PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, tenMa);
                pstmt.setInt(2, giaTri);
                pstmt.setDate(3, Date.valueOf(ngay));
                pstmt.executeUpdate();
                loadKhuyenMaiFromDatabase();
                tenKhuyenMai.clear();
                giaTriKhuyenMai.clear();
                ngayHetHan.setValue(null);
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Giá trị khuyến mãi phải là số nguyên.");
        } catch (SQLException e) {
            showError("Không thể thêm khuyến mãi", e);
        }
    }

    @FXML
    private void xoaKhuyenMai(ActionEvent event) {
        KhuyenMai selected = bangKhuyenMai.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Vui lòng chọn mã khuyến mãi cần xóa.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setContentText("Bạn có chắc chắn muốn xóa mã khuyến mãi này?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try (Connection conn = connectDB();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM khuyen_mai WHERE ten_ma = ?")) {
                stmt.setString(1, selected.getTenMa());
                stmt.executeUpdate();
                loadKhuyenMaiFromDatabase();
            } catch (SQLException e) {
                showError("Không thể xóa khuyến mãi", e);
            }
        }
    }

    @FXML
    private void taiLai(ActionEvent event) {
        loadSanPhamFromDatabase();
        loadKhuyenMaiFromDatabase();
        loadCaTrucFromDatabase();
        if (timKiem != null) timKiem.clear();
        if (tenKhuyenMai != null) tenKhuyenMai.clear();
        if (giaTriKhuyenMai != null) giaTriKhuyenMai.clear();
        if (ngayHetHan != null) ngayHetHan.setValue(null);
    }

    // ==== Điều hướng giao diện ====
    @FXML public void gotoorder(ActionEvent event) { chuyenManHinh(event, "/com/cafe/view/order.fxml", "Cafe Order"); }
    @FXML public void gotocustomers(ActionEvent event) { chuyenManHinh(event, "/com/cafe/view/customers.fxml", "Khách hàng"); }
    @FXML public void gotoreports(ActionEvent event) { chuyenManHinh(event, "/com/cafe/view/reports.fxml", "Báo cáo"); }
    @FXML public void gotoadd(ActionEvent event) { moManHinhMoi("/com/cafe/view/add.fxml", "Thêm sản phẩm", 700, 400); }

    @FXML
    public void gotofix(ActionEvent event) {
        SanPham sp = bangSanPham.getSelectionModel().getSelectedItem();
        if (sp == null) {
            showAlert(Alert.AlertType.WARNING, "Vui lòng chọn sản phẩm cần sửa.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cafe/view/fixSanPham.fxml"));
            Parent root = loader.load();
            FixSanPham controller = loader.getController();
            controller.setThongTinSanPham(sp.getId(), sp.getTen(), sp.getGia(), sp.getMoTa(), sp.getHinhAnh());
            Stage stage = new Stage();
            stage.setTitle("Sửa sản phẩm");
            stage.setScene(new Scene(root, 700, 400));
            stage.setUserData(this); // Truyền ManagementController vào Stage
            stage.show();
        } catch (IOException e) {
            showError("Không thể mở giao diện sửa sản phẩm", e);
        }
    }
    @FXML
    public void gotodoica(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cafe/view/doica.fxml"));
            Parent root = loader.load();

            // Lấy đúng controller class
            Doica controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Đổi ca");
            stage.setScene(new Scene(root, 1000, 500));
            stage.show();

        } catch (IOException e) {
            showError("Không thể mở giao diện đổi ca", e);
        }
    }

    @FXML
    public void gotodelete(ActionEvent event) {
        SanPham selected = bangSanPham.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Vui lòng chọn sản phẩm cần xóa.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cafe/view/deleteSanPham.fxml"));
            Parent root = loader.load();
            com.cafe.controller.deleteSanPham controller = loader.getController();
            controller.setSanPham(selected);
            Stage stage = new Stage();
            stage.setTitle("Xóa sản phẩm");
            stage.setScene(new Scene(root, 700, 400));
            stage.show();
        } catch (IOException e) {
            showError("Không thể mở giao diện xóa sản phẩm", e);
        }
    }

    private void chuyenManHinh(ActionEvent event, String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1200, 900);
            scene.getStylesheets().add(getClass().getResource("/com/cafe/view/Style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            showError("Không thể chuyển màn hình", e);
        }
    }

    private void moManHinhMoi(String fxmlPath, String title, int width, int height) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage newStage = new Stage();
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(getClass().getResource("/com/cafe/view/Style.css").toExternalForm());
            newStage.setScene(scene);
            newStage.setTitle(title);
            newStage.setResizable(false);
            newStage.show();
        } catch (IOException e) {
            showError("Không thể mở cửa sổ mới", e);
        }
    }

    // === Hộp thoại tiện ích ===
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