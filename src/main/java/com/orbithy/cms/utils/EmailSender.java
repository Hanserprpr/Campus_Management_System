package com.orbithy.cms.utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EmailSender {

    private static final Properties emailConfig = new Properties();

    static {
        try (InputStream input = EmailSender.class.getClassLoader().getResourceAsStream("email-config.properties")) {
            if (input == null) {
                throw new IOException("Email configuration file not found!");
            }
            emailConfig.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load email configuration.", e);
        }
    }

    /**
     * 发送邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件标题
     * @param content 邮件正文
     */
    public static void sendEmail(String to, String subject, String content) throws MessagingException {
        // 从配置文件读取 SMTP 配置
        String smtpHost = emailConfig.getProperty("mail.smtp.host");
        int smtpPort = Integer.parseInt(emailConfig.getProperty("mail.smtp.port"));
        String username = emailConfig.getProperty("mail.smtp.username");
        String password = emailConfig.getProperty("mail.smtp.password");

        // 配置 SMTP 属性
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);

        // 创建会话
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // 创建邮件消息
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));           // 发件人
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to)); // 收件人
            message.setSubject(subject);                             // 邮件标题
            message.setText(content);                                // 邮件正文

            // 发送邮件
            Transport.send(message);
            System.out.println("Email sent successfully to " + to);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Failed to send email: " + e.getMessage());
            throw e;
        }
    }
}