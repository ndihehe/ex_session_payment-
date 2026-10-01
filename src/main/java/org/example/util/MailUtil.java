package org.example.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtil {

    public static String getSenderEmail() {
        return EnvUtil.get("MAIL_USERNAME");
    }

    public static String getAppPassword() {
        return EnvUtil.get("MAIL_PASSWORD");
    }

    public static String getResendApiKey() {
        return EnvUtil.get("RESEND_API_KEY");
    }

    public static void sendMail(String to, String from,
                                String subject, String body, 
                                boolean bodyIsHTML) 
            throws MessagingException, UnsupportedEncodingException {

        // 1. Ưu tiên gửi qua Resend REST API (HTTPS port 443) nếu có API key
        String resendKey = getResendApiKey();
        if (resendKey != null && !resendKey.trim().isEmpty()) {
            try {
                boolean sent = sendViaResend(to, subject, body, resendKey);
                if (sent) {
                    System.out.println("[MailUtil] Email sent successfully via Resend HTTPS API to: " + to);
                    return;
                }
            } catch (Exception e) {
                System.err.println("[MailUtil] Resend API failed, falling back to SMTP: " + e.getMessage());
            }
        }

        // 2. Dự phòng: Gửi qua Gmail SMTP truyền thống (Port 465 SSL)
        sendViaSmtp(to, from, subject, body, bodyIsHTML);
    }
    private static boolean sendViaResend(String to, String subject, String body, String apiKey) throws Exception {
        String fromSender = EnvUtil.get("RESEND_FROM", "onboarding@resend.dev");

        String json = "{"
            + "\"from\":\"CD Store <" + fromSender + ">\","
            + "\"to\":[\"" + escapeJson(to) + "\"],"
            + "\"subject\":\"" + escapeJson(subject) + "\","
            + "\"html\":\"" + escapeJson(body) + "\""
            + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.resend.com/emails"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .timeout(Duration.ofSeconds(10))
                .build();

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return true;
        } else {
            System.err.println("[MailUtil] Resend API error (" + response.statusCode() + "): " + response.body());
            throw new RuntimeException("Resend API error: " + response.body());
        }
    }

    private static String escapeJson(String str) {
        if (str == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < ' ') {
                        String t = "000" + Integer.toHexString(c);
                        sb.append("\\u").append(t.substring(t.length() - 4));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    private static void sendViaSmtp(String to, String from,
                                   String subject, String body, 
                                   boolean bodyIsHTML) 
            throws MessagingException, UnsupportedEncodingException {

        final String username = getSenderEmail();
        final String rawPassword = getAppPassword();
        final String password = (rawPassword != null) ? rawPassword.replace(" ", "").trim() : "";

        if (username == null || username.trim().isEmpty() || password.isEmpty()) {
            throw new MessagingException("Chưa cấu hình MAIL_USERNAME hoặc MAIL_PASSWORD (hoặc RESEND_API_KEY)!");
        }

        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("java.net.preferIPv6Addresses", "false");

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
        props.put("mail.smtp.ssl.trust", "*");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setSubject(subject);

        if (bodyIsHTML) {
            message.setContent(body, "text/html; charset=UTF-8");
        } else {
            message.setText(body);
        }

        String senderName = "CD Store";
        Address fromAddress = new InternetAddress(from != null ? from : username, senderName);
        Address toAddress = new InternetAddress(to);
        message.setFrom(fromAddress);
        message.setRecipient(Message.RecipientType.TO, toAddress);

        Transport.send(message);
        System.out.println("[MailUtil] Email sent successfully via Gmail SMTP to: " + to);
    }
}
