package com.cafe.model;

import javafx.scene.layout.HBox;
import javafx.scene.control.Label;

public class InvoiceItem {
    public HBox container;
    public Label qtyLabel;
    public Label priceLabel;
    public int quantity;
    public double unitPrice;

    public InvoiceItem(HBox container, Label qtyLabel, Label priceLabel, int quantity, double unitPrice) {
        this.container = container;
        this.qtyLabel = qtyLabel;
        this.priceLabel = priceLabel;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public void update() {
        qtyLabel.setText("x" + quantity);
        priceLabel.setText(String.format("%,.0f VNĐ", unitPrice * quantity));
    }
    public double getTotal() {
        return quantity * unitPrice;
    }

}
