package com.cafe.controller;

import com.cafe.ChatBotServer.ChatServer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;

public class socket_chat {
    @FXML private TextArea responseArea;
    @FXML private TextField questionField;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    // Chỉ thử các cổng server có thể đang chạy
    private static final Integer[] PORTS_TO_TRY = {8889, 8890, 8891};
    private int currentPortIndex = 0;

    public void initialize() {
        long startTime = System.currentTimeMillis();
        connectToServer();
        Platform.runLater(() -> responseArea.appendText(
                "⏱ Khởi tạo giao diện trong " + (System.currentTimeMillis() - startTime) + "ms\n"));
    }
    @FXML
    public void gotochat(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cafe/view/chat.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Chat");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();


            Stage currentStage = (Stage) responseArea.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectToServer() {
        if (currentPortIndex >= PORTS_TO_TRY.length) {
            Platform.runLater(() -> responseArea.appendText(
                    "❌ Không thể kết nối đến server trên bất kỳ cổng nào! Đảm bảo server đang chạy.\n"));
            return;
        }

        int port = PORTS_TO_TRY[currentPortIndex];
        try {
            responseArea.appendText("🔄 Đang kết nối đến localhost:" + port + "...\n");
            socket = new Socket("localhost", port);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            Platform.runLater(() -> responseArea.appendText("✅ Đã kết nối đến server tại cổng " + port + "!\n"));

            new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        String msg = line;
                        Platform.runLater(() -> responseArea.appendText("\uD83E\uDD16 Bot: " + msg + "\n"));
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> responseArea.appendText("❌ Mất kết nối: " + e.getMessage() + "\n"));
                } finally {
                    closeConnection();
                }
            }).start();

        } catch (ConnectException e) {
            Platform.runLater(() -> responseArea.appendText(
                    "❌ Không kết nối được server trên cổng " + port + ": " + e.getMessage() + "\n"));
            currentPortIndex++;
            retryConnection();
        } catch (IOException e) {
            Platform.runLater(() -> responseArea.appendText(
                    "❌ Lỗi kết nối trên cổng " + port + ": " + e.getMessage() + "\n"));
            currentPortIndex++;
            retryConnection();
        }
    }

    private void retryConnection() {
        if (currentPortIndex < PORTS_TO_TRY.length) {
            Platform.runLater(() -> responseArea.appendText("🔄 Thử cổng tiếp theo sau 2 giây...\n"));
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(this::connectToServer);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }).start();
        }
    }

    @FXML
    private void handleSend() {
        String question = questionField.getText().trim();
        if (!question.isEmpty() && out != null) {
            out.println(question);
            responseArea.appendText("\uD83D\uDE06 Bạn: " + question + "\n");
            questionField.clear();
        } else {
            responseArea.appendText("❌ Không gửi được: " + (out == null ? "Mất kết nối" : "Câu hỏi rỗng") + "\n");
        }
    }

    private void closeConnection() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
