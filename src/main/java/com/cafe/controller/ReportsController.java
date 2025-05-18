package com.cafe.controller;

import com.cafe.model.ThongKe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class ReportsController {

    @FXML private TableView<ThongKe> reportTable;
    @FXML private TableColumn<ThongKe, String> dateColumn;
    @FXML private TableColumn<ThongKe, Integer> quantityColumn;
    @FXML private TableColumn<ThongKe, Double> totalRevenueColumn;
    @FXML private TableColumn<ThongKe, Double> averageColumn;

    @FXML private TextField txtDate;
    @FXML private TextField txtOrderCount;
    @FXML private TextField txtTotalRevenue;
    @FXML private TextField txtAverageOrderValue;

    @FXML private ListView<String> orderHistoryList;

    @FXML private LineChart<String, Number> revenueChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    @FXML private Button filterByDate;
    @FXML private Button filterByMonth;
    @FXML private Button exportReport;
    @FXML private TextField searchReport;

    private ObservableList<ThongKe> reportList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("ngay"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        totalRevenueColumn.setCellValueFactory(new PropertyValueFactory<>("doanhThu"));
        averageColumn.setCellValueFactory(new PropertyValueFactory<>("trungBinh"));

        loadReportData();

        reportTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showReportDetails(newVal);
            }
        });
    }

    private void loadReportData() {
        String query = """
            SELECT ngay,
                   SUM(so_luong) AS total_quantity,
                   SUM(tong_tien) AS total_revenue,
                   CASE WHEN SUM(so_luong) > 0 THEN SUM(tong_tien) / SUM(so_luong) ELSE 0 END AS avg_per_item
            FROM thongke
            GROUP BY ngay
            ORDER BY ngay DESC
            """;

        try (Connection conn = connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            reportList.clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Doanh thu");

            while (rs.next()) {
                String ngay = rs.getString("ngay");
                int soLuong = rs.getInt("total_quantity");
                double doanhThu = rs.getDouble("total_revenue");
                double trungBinh = rs.getDouble("avg_per_item");

                ThongKe tk = new ThongKe(ngay, soLuong, doanhThu, trungBinh);
                reportList.add(tk);

                series.getData().add(new XYChart.Data<>(ngay, doanhThu));
            }

            reportTable.setItems(reportList);
            revenueChart.getData().clear();
            revenueChart.getData().add(series);

        } catch (SQLException e) {
            showAlert("Lỗi cơ sở dữ liệu", "Không thể tải dữ liệu báo cáo: " + e.getMessage());
        }
    }

    private void showReportDetails(ThongKe report) {
        txtDate.setText(report.getNgay());
        txtOrderCount.setText(String.valueOf(report.getSoLuong()));
        txtTotalRevenue.setText(String.format("%,.0f VNĐ", report.getDoanhThu()));
        txtAverageOrderValue.setText(String.format("%,.0f VNĐ", report.getTrungBinh()));

        loadOrderHistory(report.getNgay());
    }

    private void loadOrderHistory(String ngay) {
        String query = "SELECT MaDon, NgayLap FROM HoaDon WHERE DATE(NgayLap) = ?";
        orderHistoryList.getItems().clear();

        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, ngay);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                orderHistoryList.getItems().add("Không có đơn hàng cho ngày: " + ngay);
            } else {
                while (rs.next()) {
                    String maDon = rs.getString("MaDon");
                    String ngayLap = rs.getString("NgayLap");
                    orderHistoryList.getItems().add("Mã đơn: " + maDon + " - Ngày: " + ngayLap);
                }
            }

        } catch (SQLException e) {
            orderHistoryList.getItems().add("Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    @FXML
    private void viewInvoiceDetails() {
        String selectedItem = orderHistoryList.getSelectionModel().getSelectedItem();
        if (selectedItem == null || !selectedItem.startsWith("Mã đơn: ")) {
            showAlert("Lỗi", "Vui lòng chọn một hóa đơn hợp lệ từ danh sách!");
            return;
        }

        String maDon = selectedItem.replace("Mã đơn: ", "").split(" - ")[0];
        showInvoiceDetails(maDon);
    }

    private void showInvoiceDetails(String maDon) {
        try {
            java.net.URL fxmlLocation = getClass().getResource("/com/cafe/view/InvoiceDetails.fxml");

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            if (loader.getLocation() == null) {
                throw new IOException(" ");
            }
            Parent root = loader.load();
            InvoiceDetailsController controller = loader.getController();
            controller.loadInvoiceDetails(maDon);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Chi tiết hóa đơn: " + maDon);
            stage.setWidth(600);
            stage.setHeight(400);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể mở chi tiết hóa đơn: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi cơ sở dữ liệu", "Không thể tải chi tiết hóa đơn: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Connection connectDB() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:13306/coffee_shop", "root", "");
    }

    private void switchScene(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            root.getStylesheets().add(getClass().getResource("/com/cafe/view/Style.css").toExternalForm());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setWidth(1200);
            stage.setHeight(900);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            showAlert("Lỗi", "Không thể chuyển scene tới " + fxmlPath + ": " + e.getMessage());
        }
    }

    @FXML
    private void gotoManagement(ActionEvent event) {
        switchScene(event, "/com/cafe/view/management.fxml");
    }

    @FXML
    private void gotoorder(ActionEvent event) {
        switchScene(event, "/com/cafe/view/order.fxml");
    }

    @FXML
    private void gotocustomers(ActionEvent event) {
        switchScene(event, "/com/cafe/view/customers.fxml");
    }

    @FXML
    private void filterByDate() {
        showAlert("Thông báo", "Chức năng lọc theo ngày chưa được triển khai.");
    }

    @FXML
    private void filterByMonth() {
        showAlert("Thông báo", "Chức năng lọc theo tháng chưa được triển khai.");
    }

    @FXML
    private void exportReport() {
        showAlert("Thông báo", "Chức năng xuất báo cáo chưa được triển khai.");
    }
}