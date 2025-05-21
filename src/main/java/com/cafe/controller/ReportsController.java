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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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

    @FXML private TextField filterReportList;

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
        filterReportList.textProperty().addListener((obs, oldVal, newVal) -> {
            filterReportList(newVal);
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

    private void loadReportByMonth() {
        String query = """
            SELECT DATE_FORMAT(ngay, '%Y-%m') AS month,
                   SUM(so_luong) AS total_quantity,
                   SUM(tong_tien) AS total_revenue,
                   CASE WHEN SUM(so_luong) > 0 THEN SUM(tong_tien) / SUM(so_luong) ELSE 0 END AS avg_per_item
            FROM thongke
            GROUP BY month
            ORDER BY month DESC
            """;

        try (Connection conn = connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            reportList.clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Doanh thu theo tháng");

            while (rs.next()) {
                String thang = rs.getString("month");
                int soLuong = rs.getInt("total_quantity");
                double doanhThu = rs.getDouble("total_revenue");
                double trungBinh = rs.getDouble("avg_per_item");

                ThongKe tk = new ThongKe(thang, soLuong, doanhThu, trungBinh);
                reportList.add(tk);

                series.getData().add(new XYChart.Data<>(thang, doanhThu));
            }

            reportTable.setItems(reportList);
            revenueChart.getData().clear();
            revenueChart.getData().add(series);

        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể tải dữ liệu theo tháng: " + e.getMessage());
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
            if (loader.getLocation() == null) throw new IOException("Không tìm thấy FXML");

            Parent root = loader.load();
            InvoiceDetailsController controller = loader.getController();
            controller.loadInvoiceDetails(maDon);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Chi tiết hóa đơn: " + maDon);
            stage.setWidth(600);
            stage.setHeight(400);
            stage.show();

        } catch  (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể mở chi tiết hóa đơn: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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
        loadReportData();
    }

    @FXML
    private void filterByMonth() {
        loadReportByMonth();
    }

    @FXML
    private void exportReport() {
        if (reportList.isEmpty()) {
            showAlert("Thông báo", "Không có dữ liệu để xuất.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu báo cáo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("bao_cao_doanh_thu.csv");

        File file = fileChooser.showSaveDialog(null);
        if (file == null) return;

        try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
            writer.println("Ngày,Số lượng,Doanh thu,Trung bình");
            for (ThongKe tk : reportList) {
                writer.printf("%s,%d,%.0f,%.0f%n",
                        tk.getNgay(), tk.getSoLuong(), tk.getDoanhThu(), tk.getTrungBinh());
            }

            showAlert("Thành công", "Đã xuất báo cáo thành công tới:\n" + file.getAbsolutePath());

        } catch (IOException e) {
            showAlert("Lỗi", "Không thể xuất báo cáo: " + e.getMessage());
        }
    }
    private void filterReportList(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            reportTable.setItems(reportList);
            return;
        }

        String lowerKeyword = keyword.toLowerCase();
        ObservableList<ThongKe> filteredList = FXCollections.observableArrayList();

        for (ThongKe tk : reportList) {
            String ngay = tk.getNgay().toLowerCase();
            String soLuong = String.valueOf(tk.getSoLuong());
            String doanhThu = String.format("%.0f", tk.getDoanhThu());
            String trungBinh = String.format("%.0f", tk.getTrungBinh());

            if (ngay.contains(lowerKeyword)
                    || soLuong.contains(lowerKeyword)
                    || doanhThu.contains(lowerKeyword)
                    || trungBinh.contains(lowerKeyword)) {
                filteredList.add(tk);
            }
        }

        reportTable.setItems(filteredList);
    }

}
