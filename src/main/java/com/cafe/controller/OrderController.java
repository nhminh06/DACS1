package com.cafe.controller;

import com.cafe.model.InvoiceItem;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class OrderController {
    @FXML
    private VBox invoiceItems;

    @FXML
    private Label totalAmount;

    @FXML
    private FlowPane productContainer;

    @FXML
    private Label discountAmount;

    @FXML
    private FlowPane tableContainer;

    private Map<String, InvoiceItem> invoiceMap = new HashMap<>();
    private String selectedTable = null;
    private double phanTramGiam = 0;

    private Connection connectDB() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:13306/coffee_shop", "root", "");
    }

    @FXML
    public void initialize() {
        loadSanPhamToUI();
        loadTablesToUI();
        productContainer.setPrefWrapLength(500);
    }

    private void loadSanPhamToUI() {
        productContainer.getChildren().clear();
        String sql = "SELECT ten_san_pham, gia, mo_ta, hinh_anh FROM douong";
        try (Connection conn = connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String ten = rs.getString("ten_san_pham");
                double gia = rs.getDouble("gia");
                String moTa = rs.getString("mo_ta");
                String hinhAnh = rs.getString("hinh_anh");
                VBox productBox = createProductBox(ten, gia, moTa, hinhAnh);
                productContainer.getChildren().add(productBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createProductBox(String ten, double gia, String moTa, String hinhAnh) {
        VBox box = new VBox(5);
        box.getStyleClass().add("product-box");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);

        if (hinhAnh != null && !hinhAnh.isEmpty()) {
            try {
                File imgFile = new File(hinhAnh);
                if (imgFile.exists()) {
                    Image img = new Image(imgFile.toURI().toString(), true);
                    imageView.setImage(img);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Label nameLabel = new Label(ten);
        nameLabel.getStyleClass().add("product-name");

        Label priceLabel = new Label(String.format("%,.0f VNĐ", gia));
        priceLabel.getStyleClass().add("product-price");

        Button addButton = new Button("Thêm");
        addButton.getStyleClass().add("add-button");
        addButton.setOnAction(e -> addToInvoice(ten, gia));

        box.getChildren().addAll(imageView, nameLabel, priceLabel, addButton);
        return box;
    }

    private void loadTablesToUI() {
        tableContainer.getChildren().clear();
        String sql = "SELECT id_ban, trang_thai FROM ban WHERE id_ban BETWEEN 1 AND 10";
        try (Connection conn = connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int idBan = rs.getInt("id_ban");
                String trangThai = rs.getString("trang_thai");
                VBox tableBox = createTableBox(idBan, trangThai);
                tableContainer.getChildren().add(tableBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createTableBox(int idBan, String trangThai) {
        VBox box = new VBox(5);
        box.getStyleClass().add("o-ban");

        Label banLabel = new Label("Bàn " + idBan);
        banLabel.getStyleClass().add("ban-label");

        Label statusLabel = new Label(trangThai.equals("Trống") ? "Trống" : "Đang dùng");
        statusLabel.getStyleClass().add("trang-thai");
        if (trangThai.equals("Đang dùng")) statusLabel.getStyleClass().add("trang-thai-occupied");
        else statusLabel.getStyleClass().add("trang-thai");

        if (trangThai.equals("Đang dùng")) {
            Button selectButton = new Button("Chọn");
            selectButton.getStyleClass().add("add-button");
            selectButton.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Xác nhận");
                alert.setHeaderText("Bàn đang được dùng");
                alert.setContentText("Bàn đã dùng xong chưa?");
                if (alert.showAndWait().get() == ButtonType.OK) {
                    updateTableStatus(idBan, "Trống");
                    loadTablesToUI();
                }
            });
            box.getChildren().addAll(banLabel, statusLabel, selectButton);
        } else {
            box.getChildren().addAll(banLabel, statusLabel);
        }
        return box;
    }

    private void updateTableStatus(int idBan, String newStatus) {
        String sql = "UPDATE ban SET trang_thai = ? WHERE id_ban = ?";
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, idBan);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showTableSelection() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cafe/view/chonban.fxml"));
            Parent root = loader.load();
            Chonban controller = loader.getController();
            controller.setOrderController(this);
            Stage stage = new Stage();
            stage.setTitle("Chọn bàn");
            Scene scene = new Scene(root, 600, 400);
            scene.getStylesheets().add(getClass().getResource("/com/cafe/view/Style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void selectTable(int idBan) {
        selectedTable = "Bàn " + idBan;
    }

    @FXML
    public void handlePayment(ActionEvent event) {
        if (selectedTable == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng chọn bàn trước khi thanh toán!");
            alert.showAndWait();
            return; // Dừng lại nếu không có bàn được chọn
        }

        if (invoiceMap.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng thêm ít nhất 1 mặt hàng trước khi thanh toán!");
            alert.showAndWait();
            return; // Dừng lại nếu không có mặt hàng nào
        }

        // Cập nhật trạng thái bàn
        int idBan = Integer.parseInt(selectedTable.replace("Bàn ", ""));
        updateTableStatus(idBan, "Đang dùng");
        loadTablesToUI();

        // Mở cửa sổ hóa đơn
        try {
            URL resource = getClass().getResource("/com/cafe/view/Invoice.fxml");
            if (resource == null) {
                throw new IOException(" ");
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            InvoiceController invoiceController = loader.getController();
            invoiceController.setOrderController(this);
            invoiceController.loadInvoiceData(selectedTable, invoiceMap, phanTramGiam, totalAmount.getText());

            Stage stage = new Stage();
            stage.setTitle("Hóa đơn");
            Scene scene = new Scene(root, 400, 800);
            URL cssUrl = getClass().getResource("/com/cafe/view/Style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println(" ");
            }
            stage.setScene(scene);
            stage.showAndWait(); // Chờ người dùng đóng cửa sổ hóa đơn

            // Sau khi đóng cửa sổ hóa đơn, reset hóa đơn
            invoiceItems.getChildren().clear();
            invoiceMap.clear();
            updateTotal();
            selectedTable = null;
        } catch (IOException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Lỗi");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Không thể tải hóa đơn: " + e.getMessage());
            errorAlert.showAndWait();
        }
    }

    private void addToInvoice(String ten, double gia) {
        if (invoiceMap.containsKey(ten)) {
            InvoiceItem item = invoiceMap.get(ten);
            item.quantity++;
            item.update();
        } else {
            Button minusButton = new Button("-");
            minusButton.getStyleClass().add("minus-button");
            minusButton.setMinWidth(15);
            minusButton.setMaxWidth(15);
            minusButton.setMinHeight(15);
            minusButton.setMaxHeight(15);

            Label tenLabel = new Label(ten);
            tenLabel.setMinWidth(100);
            tenLabel.getStyleClass().add("product-name");

            Label qtyLabel = new Label("x1");
            qtyLabel.setMinWidth(30);
            qtyLabel.getStyleClass().add("summary-value");

            Label giaLabel = new Label(String.format("%,.0f VNĐ", gia));
            giaLabel.setMinWidth(80);
            giaLabel.getStyleClass().add("summary-value");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox nameBox = new HBox(5, minusButton, tenLabel);
            nameBox.setAlignment(Pos.CENTER_LEFT);

            HBox line = new HBox(10, nameBox, spacer, qtyLabel, giaLabel);
            line.setAlignment(Pos.CENTER_LEFT);

            invoiceItems.getChildren().add(line);

            InvoiceItem item = new InvoiceItem(line, qtyLabel, giaLabel, 1, gia);
            invoiceMap.put(ten, item);

            minusButton.setOnAction(event -> {
                item.quantity--;
                if (item.quantity <= 0) {
                    invoiceItems.getChildren().remove(item.container);
                    invoiceMap.remove(ten);
                } else {
                    item.update();
                }
                updateTotal();
            });
        }
        updateTotal();
    }

    private void updateTotal() {
        double total = 0;
        for (InvoiceItem item : invoiceMap.values()) {
            total += item.quantity * item.unitPrice;
        }
        double tongSauGiam = total * (1 - phanTramGiam / 100);
        totalAmount.setText(String.format("%,.0f VNĐ", tongSauGiam));
    }

    public void setKhuyenMai(double giamPhanTram) {
        this.phanTramGiam = giamPhanTram;
        updateTotal();
    }

    @FXML
    public void gotoManagement(ActionEvent event) {
        switchScene(event, "/com/cafe/view/management.fxml");
    }

    @FXML
    public void gotocustomers(ActionEvent event) {
        switchScene(event, "/com/cafe/view/customers.fxml");
    }

    @FXML
    public void gotoreports(ActionEvent event) {
        switchScene(event, "/com/cafe/view/reports.fxml");
    }

    @FXML
    public void gotokhuyenmai(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cafe/view/chonkhuyenmai.fxml"));
            Parent root = loader.load();
            com.cafe.controller.Chonkhuyenmai controller = loader.getController();
            controller.setOrderController(this);
            Stage stage = new Stage();
            stage.setTitle("Chọn khuyến mãi");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void switchScene(ActionEvent event, String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/cafe/view/Style.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Cafe Order");
            primaryStage.setWidth(1200);
            primaryStage.setHeight(900);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void HienThiKhuyenMai(String tenMa) {
        String sql = "SELECT gia_tri FROM khuyen_mai WHERE ten_ma = ? AND ngay_het_han >= CURDATE()";
        try (Connection conn = connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tenMa);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double giaTri = rs.getDouble("gia_tri");
                setKhuyenMai(giaTri);
                discountAmount.setText(String.format("%.0f %%", giaTri));
            } else {
                setKhuyenMai(0);
                discountAmount.setText("0 %");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            discountAmount.setText("0 %");
        }
    }
}