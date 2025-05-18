package com.cafe.util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailSender {
    // Email gửi (cố định trong code)
    private static final String FROM_EMAIL = "mnhat0034@gmail.com";  // Thay bằng email của bạn
    private static final String APP_PASSWORD = "rqxr eqjw fkoe wejf";  // Thay mật khẩu app

    public static void sendVerificationCode(String toEmail, String code) throws MessagingException {
        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("Mã xác nhận");
        message.setText("Mã xác nhận của bạn là: " + code);

        Transport.send(message);
    }
}
