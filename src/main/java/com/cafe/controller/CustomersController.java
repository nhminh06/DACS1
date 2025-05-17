package com.cafe.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.*;
import com.cafe.model.NhanVien;

public class CustomersController {

    @FXML private TableView<NhanVien> bangNhanVien;
    @FXML private TableColumn<NhanVien, String> cotMaNV, cotTenNV, cotChucVu, cotSdt, cotEmail;
    @FXML private TableColumn<NhanVien, Double> cotLuong;

    @FXML private TextField txtMaNV, txtTenNV, txtChucVu, txtSdt, txtEmail, txtLuong;
    @FXML private ComboBox<String> comboNguyenNhan;
    @FXML private TextField txtSoTien;
    @FXML private TextField timNhanVien;

    private ObservableList<NhanVien> danhSach = FXCollections.observableArrayList();

    private Connection connectDB() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:13306/coffee_shop", "root", "");
    }

    @FXML
    public void initialize() {
        cotMaNV.setCellValueFactory(cell -> cell.getValue().maNVProperty());
        cotTenNV.setCellValueFactory(cell -> cell.getValue().hoTenProperty());
        cotChucVu.setCellValueFactory(cell -> cell.getValue().chucVuProperty());
        cotSdt.setCellValueFactory(cell -> cell.getValue().sdtProperty());
        cotEmail.setCellValueFactory(cell -> cell.getValue().emailProperty());
        cotLuong.setCellValueFactory(cell -> cell.getValue().luongProperty().asObject());
        cotLuong.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null :
                        String.format("%,.0f VNĐ", item));
            }
        });

        bangNhanVien.setItems(danhSach);
        loadData();

        bangNhanVien.setOnMouseClicked(this::hienThiChiTiet);
        timNhanVien.textProperty().addListener((obs, oldText, newText) -> timKiemNhanVien());
    }

    private void loadData() {
        danhSach.clear();
        String query = "SELECT * FROM nhanvien";
        try (Connection conn = connectDB(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                danhSach.add(new NhanVien(
                        rs.getString("MaNV"),
                        rs.getString("HoTen"),
                        rs.getString("ChucVu"),
                        rs.getString("Sdt"),
                        rs.getString("Email"),
                        rs.getDouble("Luong")
                ));
            }

            // Sắp xếp danh sách theo phần số của MaNV
            danhSach.sort((nv1, nv2) -> {
                try {
                    int num1 = Integer.parseInt(nv1.getMaNV().replace("NV", ""));
                    int num2 = Integer.parseInt(nv2.getMaNV().replace("NV", ""));
                    return Integer.compare(num1, num2);
                } catch (NumberFormatException e) {
                    // Nếu không parse được số, so sánh chuỗi gốc
                    return nv1.getMaNV().compareTo(nv2.getMaNV());
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void hienThiChiTiet(MouseEvent event) {
        NhanVien nv = bangNhanVien.getSelectionModel().getSelectedItem();
        if (nv != null) {
            txtMaNV.setText(nv.getMaNV());
            txtTenNV.setText(nv.getHoTen());
            txtChucVu.setText(nv.getChucVu());
            txtSdt.setText(nv.getSdt());
            txtEmail.setText(nv.getEmail());
            txtLuong.setText(String.format("%,.0f", nv.getLuong()));
        }
    }

    private void timKiemNhanVien() {
        String tuKhoa = timNhanVien.getText().toLowerCase();
        ObservableList<NhanVien> ketQua = FXCollections.observableArrayList();

        for (NhanVien nv : danhSach) {
            if (nv.getHoTen().toLowerCase().contains(tuKhoa) ||
                    nv.getMaNV().toLowerCase().contains(tuKhoa) ||
                    nv.getSdt().toLowerCase().contains(tuKhoa) ||
                    nv.getEmail().toLowerCase().contains(tuKhoa) ||
                    nv.getChucVu().toLowerCase().contains(tuKhoa)) {
                ketQua.add(nv);
            }
        }

        bangNhanVien.setItems(ketQua);
    }

    @FXML
    private void tangLuong() {
        thayDoiLuong(true);
    }

    @FXML
    private void giamLuong() {
        thayDoiLuong(false);
    }

    private void thayDoiLuong(boolean isTang) {
        String maNV = txtMaNV.getText();
        String nguyenNhan = comboNguyenNhan.getValue();
        double soTien;

        try {
            soTien = Double.parseDouble(txtSoTien.getText());
            if (!isTang) soTien = -soTien;
        } catch (NumberFormatException e) {
            showAlert("Vui lòng nhập số tiền hợp lệ.");
            return;
        }

        String sql = "UPDATE NhanVien SET Luong = Luong + ? WHERE MaNV = ?";
        try (Connection conn = connectDB(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, soTien);
            stmt.setString(2, maNV);
            stmt.executeUpdate();
            loadData();
            txtSoTien.clear();
            comboNguyenNhan.setValue(null);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Không thể cập nhật lương!");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    private void taiLaiTrang() {
        loadData();
        bangNhanVien.setItems(danhSach);
        timNhanVien.clear();
        txtMaNV.clear();
        txtTenNV.clear();
        txtChucVu.clear();
        txtSdt.clear();
        txtEmail.clear();
        txtLuong.clear();
        comboNguyenNhan.setValue(null);
        txtSoTien.clear();
    }

    // ==== Các hàm chuyển cảnh ====

    @FXML
    public void gotoManagement(ActionEvent event) {
        chuyenManHinh(event, "/com/cafe/view/management.fxml", 1200, 900);
    }

    @FXML
    public void gotoorder(ActionEvent event) {
        chuyenManHinh(event, "/com/cafe/view/order.fxml", 1200, 900);
    }

    @FXML
    public void gotoreports(ActionEvent event) {
        chuyenManHinh(event, "/com/cafe/view/reports.fxml", 1200, 900);
    }

    @FXML
    public void gotoaddnhanvien(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cafe/view/addnhanvien.fxml"));
            Parent root = loader.load();

            // Lấy controller của addNhanVien và truyền danhSach
            addNhanVien controller = loader.getController();
            controller.setDanhSach(danhSach);

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/cafe/view/Style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Thêm Nhân Viên");
            stage.setWidth(700);
            stage.setHeight(400);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void gotofixnhanvien(ActionEvent event) {
        NhanVien selected = bangNhanVien.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn nhân viên cần sửa!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cafe/view/fixnhanvien.fxml"));
            Parent root = loader.load();

            // Lấy controller của fixNhanVien và truyền dữ liệu
            fixNhanVien controller = loader.getController();
            controller.setThongTinNhanVien(
                    selected.getMaNV(),
                    selected.getHoTen(),
                    selected.getSdt(),
                    selected.getEmail(),
                    selected.getChucVu()
            );

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/cafe/view/Style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Sửa Nhân Viên");
            stage.setWidth(700);
            stage.setHeight(400);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void gotodeletenhanvien(ActionEvent event) {
        NhanVien selected = bangNhanVien.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn nhân viên cần xóa!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cafe/view/deletenhanvien.fxml"));
            Parent root = loader.load();

            // Gửi dữ liệu sang controller của deletenhanvien
            deletenhanvienController controller = loader.getController();
            controller.setNhanVien(selected);

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/cafe/view/Style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Xóa Nhân Viên");
            stage.setWidth(400);
            stage.setHeight(250);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ==== Hàm phụ ====

    private void chuyenManHinh(ActionEvent event, String fxmlPath, int width, int height) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/cafe/view/Style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Cafe Order");
            stage.setWidth(width);
            stage.setHeight(height);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void moManHinhPhu(String fxmlPath, int width, int height) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/cafe/view/Style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Cafe Order");
            stage.setWidth(width);
            stage.setHeight(height);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}