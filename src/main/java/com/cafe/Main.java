package com.cafe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/cafe/view/login.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/cafe/view/Style.css").toExternalForm());
        primaryStage.setScene(scene); primaryStage.setTitle("Hệ thống quản lý quán cà phê"); primaryStage.setWidth(800); primaryStage.setHeight(437); primaryStage.setResizable(false); primaryStage.show();


        primaryStage.setTitle("Cafe Order");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
