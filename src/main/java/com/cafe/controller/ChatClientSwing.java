package com.cafe.controller;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ChatClientSwing extends JFrame {
    private static ChatClientSwing instance;

    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // Singleton: private constructor
    private ChatClientSwing() {
        setTitle("🧃 Chat Client");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        messageField = new JTextField();
        sendButton = new JButton("Gửi");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());

        connectToServer();
    }

    // Phương thức lấy instance duy nhất
    public static synchronized ChatClientSwing getInstance() {
        if (instance == null) {
            instance = new ChatClientSwing();
        }
        return instance;
    }

    private void connectToServer() {
        try {
            String serverIP = JOptionPane.showInputDialog(this, "Nhập IP server:");
            if (serverIP == null || serverIP.trim().isEmpty()) {
                serverIP = "127.0.0.1"; // IP mặc định nếu người dùng không nhập
            }

            String name = JOptionPane.showInputDialog(this, "Nhập tên của bạn:");
            if (name == null || name.trim().isEmpty()) {
                name = "Người dùng";
            }

            socket = new Socket(serverIP, 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(name); // Gửi tên cho server

            // Thread nhận tin nhắn từ server và cập nhật lên JTextArea
            new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        chatArea.append(line + "\n");
                    }
                } catch (IOException e) {
                    chatArea.append("❌ Mất kết nối tới server\n");
                }
            }).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Không thể kết nối tới server", "Lỗi", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            out.println(message);
            messageField.setText("");
        }
    }

    // Mở cửa sổ chat, gọi từ nơi khác trong app nếu cần
    public void moiban() {
        SwingUtilities.invokeLater(() -> {
            ChatClientSwing window = ChatClientSwing.getInstance();
            if (!window.isVisible()) {
                window.setVisible(true);
            }
            window.toFront();
            window.requestFocus();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatClientSwing.getInstance().setVisible(true);
        });
    }
}
