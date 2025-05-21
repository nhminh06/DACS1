package com.cafe.ChatBotServer;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    private static final Integer[] PORTS_TO_TRY = {8889, 8890, 8891};
    private static final ExecutorService executor = Executors.newFixedThreadPool(5);

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        ServerSocket serverSocket = null;
        for (int port : PORTS_TO_TRY) {
            try {
                serverSocket = new ServerSocket(port);
                System.out.println("ChatBot Server khởi động tại cổng " + port + " trong " +
                        (System.currentTimeMillis() - startTime) + "ms");
                System.out.println("Đang chờ client kết nối...");
                break;
            } catch (IOException e) {
                System.err.println("Không thể bind vào cổng " + port + ": " + e.getMessage());
                if (port == PORTS_TO_TRY[PORTS_TO_TRY.length - 1]) {
                    System.err.println("Không thể tìm cổng khả dụng!");
                    return;
                }
            }
        }

        if (serverSocket != null) {
            try {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client kết nối từ " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                    executor.submit(() -> handleClient(clientSocket));
                }
            } catch (IOException e) {
                System.err.println("Lỗi khi chấp nhận kết nối: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                executor.shutdown();
            }
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true)
        ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {

                String answer = generateAnswer(inputLine);
                out.println(answer);

            }
            System.out.println("Đã đóng kết nối với " + clientSocket.getInetAddress());
        } catch (IOException e) {
            System.err.println("Lỗi với client " + clientSocket.getInetAddress() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String generateAnswer(String question) {
        String q = question.trim().toLowerCase();

        try (Connection conn = connectDB()) {

            if (q.contains("doanh thu hôm nay")) {
                String sql = "SELECT SUM(tong_tien) FROM thongke WHERE ngay = CURDATE()";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        double total = rs.getDouble(1);
                        return "🧾 Doanh thu hôm nay là " + String.format("%,.0f", total) + " VNĐ.";
                    }
                }

            } else if (q.contains("tổng số đơn hôm nay")) {
                String sql = "SELECT COUNT(*) FROM hoadon WHERE DATE(NgayLap) = CURDATE()";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        return "📦 Tổng số đơn hàng hôm nay là: " + count + " đơn.";
                    }
                }

            } else if (q.contains("xem bảng kế hoạch")) {
               return "❌ Quyền hạng không đủ";

            } else if (q.contains("doanh thu theo ngày")) {
                String[] parts = q.split(" ");
                String date = parts[parts.length - 1];
                String sql = "SELECT SUM(tong_tien) FROM thongke WHERE ngay = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, date);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        double total = rs.getDouble(1);
                        return "💰 Doanh thu ngày " + date + " là " + String.format("%,.0f", total) + " VNĐ.";
                    } else {
                        return "❌ Không có dữ liệu cho ngày " + date;
                    }
                }

            } else if (q.contains("giờ mở cửa")) {
                return "🕖 Quán mở cửa từ 7h đến 22h mỗi ngày.";

            }



            else if (q.contains("app đã gặp lỗi")) {
                return "📍 Đã thông báo đến nhân viên bảo trì .";

            }
            else if(q.contains("hello")){
                return "🤖 xin chào.";
            }
         else if (q.contains("xin chào")) {
            return "\uD83D\uDC7B hello .";

        }else {
             return "\uD83D\uDC7E với trí thông minh giản dị của tôi thì tôi không biết";
            }

        } catch (SQLException e) {
            return "⚠️ Lỗi kết nối cơ sở dữ liệu: " + e.getMessage();
        }

        return "🤖 Xin lỗi, không có thông tin phù hợp.";
    }

    private static Connection connectDB() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:13306/coffee_shop", "root", "");
    }
}
