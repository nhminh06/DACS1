package com.cafe.controller;

import com.cafe.model.InvoiceItem;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class InvoiceController {

    @FXML
    private VBox invoiceItems;

    @FXML
    private Label totalAmount;

    @FXML
    private Label discountAmount;

    @FXML
    private Label dateTimeLabel;

    private OrderController orderController;
    private boolean isConfirmed = false; // Biến để theo dõi trạng thái xác nhận

    public void setOrderController(OrderController orderController) {
        this.orderController = orderController;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void loadInvoiceData(String selectedTable, Map<String, InvoiceItem> invoiceMap, double phanTramGiam, String totalText) {
        // Hiển thị ngày giờ
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        dateTimeLabel.setText("Ngày: " + now.format(formatter));

        // Hiển thị bàn đã chọn
        Label tableLabel = new Label("Bàn: " + selectedTable);
        tableLabel.getStyleClass().add("ban-label");
        invoiceItems.getChildren().add(0, tableLabel); // Thêm vào đầu danh sách

        // Hiển thị các món trong hóa đơn
        invoiceItems.getChildren().clear(); // Xóa nội dung cũ
        invoiceItems.getChildren().add(tableLabel); // Thêm lại label bàn

        for (Map.Entry<String, InvoiceItem> entry : invoiceMap.entrySet()) {
            String ten = entry.getKey();
            InvoiceItem item = entry.getValue();

            Label tenLabel = new Label(ten);
            tenLabel.setMinWidth(150);
            tenLabel.getStyleClass().add("product-name");

            Label qtyLabel = new Label("x" + item.quantity);
            qtyLabel.setMinWidth(30);
            qtyLabel.getStyleClass().add("summary-value");

            Label giaLabel = new Label(String.format("%,.0f VNĐ", item.quantity * item.unitPrice));
            giaLabel.setMinWidth(80);
            giaLabel.getStyleClass().add("summary-value");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox line = new HBox(10, tenLabel, spacer, qtyLabel, giaLabel);
            line.setAlignment(Pos.CENTER_LEFT);

            invoiceItems.getChildren().add(line);
        }

        // Hiển thị giảm giá và tổng cộng
        discountAmount.setText(String.format("%.0f %%", phanTramGiam));
        totalAmount.setText(totalText);
    }

    @FXML
    public void closeWindow() {
        isConfirmed = true; // Đặt isConfirmed thành true khi bấm "Xác nhận"
        Stage stage = (Stage) invoiceItems.getScene().getWindow();
        stage.close();
    }
}