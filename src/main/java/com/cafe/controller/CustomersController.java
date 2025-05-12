package com.cafe.controller;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class CustomersController {
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
    public void gotoorder(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/cafe/view/order.fxml"));
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
    @FXML
    public void gotoaddnhanvien(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/cafe/view/addnhanvien.fxml"));
            Stage newStage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/cafe/view/Style.css").toExternalForm());

            newStage.setScene(scene);
            newStage.setTitle("Cafe Order");
            newStage.setWidth(700);
            newStage.setHeight(400);
            newStage.setResizable(false);
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void gotofixnhanvien(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/cafe/view/fixnhanvien.fxml"));
            Stage newStage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/cafe/view/Style.css").toExternalForm());

            newStage.setScene(scene);
            newStage.setTitle("Cafe Order");
            newStage.setWidth(700);
            newStage.setHeight(400);
            newStage.setResizable(false);
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void gotodeletenhanvien(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/cafe/view/deletenhanvien.fxml"));
            Stage newStage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/cafe/view/Style.css").toExternalForm());

            newStage.setScene(scene);
            newStage.setTitle("Cafe Order");
            newStage.setWidth(700);
            newStage.setHeight(400);
            newStage.setResizable(false);
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}