package com.cafe.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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
            // Yêu cầu người dùng nhập IP và tên
            String serverIP = JOptionPane.showInputDialog("Nhập IP server:", "10.50.105.245");
            if (serverIP == null || serverIP.trim().isEmpty()) {
                serverIP = "10.50.105.245";
            }

            String ten = JOptionPane.showInputDialog("Nhập tên của bạn:");

            socket = new Socket(serverIP, 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(ten);

            // Luồng riêng để nhận tin nhắn
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

    private void sendMessage() {
        String message = skchat.getText().trim();
        if (!message.isEmpty()) {
            out.println(message);
            skchat.clear();
        }
    }
}
