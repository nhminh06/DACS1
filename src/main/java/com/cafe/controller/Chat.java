package com.cafe.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class Chat {
    @FXML private TextField skchat;
    @FXML private TextArea chatArea;
    @FXML private Button sendButton;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    @FXML
    private void initialize() {
        try {
            // Yêu cầu người dùng nhập IP và tên (không có giá trị mặc định trong ô nhập)
            String serverIP = JOptionPane.showInputDialog("Nhập IP server:");
            if (serverIP == null || serverIP.trim().isEmpty()) {
                serverIP = "127.0.0.1"; // IP mặc định nếu người dùng không nhập
            }

            String ten = JOptionPane.showInputDialog("Nhập tên của bạn:");
            if (ten == null || ten.trim().isEmpty()) {
                ten = "Người dùng"; // Tên mặc định
            }

            socket = new Socket(serverIP, 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(ten);

            // Luồng nhận tin nhắn từ server
            Thread nhanTin = new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        String finalLine = line;
                        Platform.runLater(() -> chatArea.appendText(finalLine + "\n"));
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> chatArea.appendText("❌ Mất kết nối tới server\n"));
                }
            });
            nhanTin.setDaemon(true);
            nhanTin.start();

        } catch (IOException e) {
            chatArea.appendText("❌ Không thể kết nối tới server\n");
            e.printStackTrace();
        }

        sendButton.setOnAction(event -> sendMessage());
        skchat.setOnAction(event -> sendMessage()); // Nhấn Enter cũng gửi
    }
    @FXML
    public void gotochat(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cafe/view/socket_chat.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Chat");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();

            // Đóng cửa sổ hiện tại (từ nút hoặc field hiện tại)
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void moiban(ActionEvent event) {
        // Mở cửa sổ Swing chat client trên thread riêng cho Swing (EDT)
        new Thread(() -> {
            ChatClientSwing.getInstance().moiban();
        }).start();
    }



    private void sendMessage() {
        String message = skchat.getText().trim();
        if (!message.isEmpty()) {
            out.println(message);
            skchat.clear();
        }
    }
}
