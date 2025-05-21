package com.cafe.ChatBotServer;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServerv2 {
    private static final int PORT = 12345;
    private static final List<ClientHandler> danhSachClient = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server đang chạy tại cổng " + PORT);

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Kết nối mới: " + socket);
            ClientHandler client = new ClientHandler(socket);
            danhSachClient.add(client);
            client.start();
        }
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String tenClient = in.readLine();
                guiChoTatCa("🔔 " + tenClient + " đã tham gia trò chuyện!");

                String tinNhan;
                while ((tinNhan = in.readLine()) != null) {
                    guiChoTatCa("💬 " + tenClient + ": " + tinNhan);
                }
            } catch (IOException e) {
                System.out.println("Lỗi kết nối: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {}
                danhSachClient.remove(this);
            }
        }

        void guiChoTatCa(String message) {
            synchronized (danhSachClient) {
                for (ClientHandler client : danhSachClient) {
                    client.out.println(message);
                }
            }
        }
    }
}
