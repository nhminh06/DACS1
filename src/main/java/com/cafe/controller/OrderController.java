package com.cafe.controller;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class OrderController {
    @FXML
    public void gotoManagement(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/cafe/view/management.fxml"));
            Stage primaryStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/cafe/view/Style.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Cafe Order");
            primaryStage.setScene(scene); primaryStage.setTitle("Cafe Order"); primaryStage.setWidth(1200); primaryStage.setHeight(900); primaryStage.setResizable(false); primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void gotocustomers(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/cafe/view/customers.fxml"));
            Stage primaryStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/cafe/view/Style.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Cafe Order");
            primaryStage.setScene(scene); primaryStage.setTitle("Cafe Order"); primaryStage.setWidth(1200); primaryStage.setHeight(900); primaryStage.setResizable(false); primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void gotoreports(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/cafe/view/reports.fxml"));
            Stage primaryStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/cafe/view/Style.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Cafe Order");
            primaryStage.setScene(scene); primaryStage.setTitle("Cafe Order"); primaryStage.setWidth(1200); primaryStage.setHeight(900); primaryStage.setResizable(false); primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}