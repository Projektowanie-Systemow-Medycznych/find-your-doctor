package com.example.model;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailSender {

    @Value("${FROM_MAIL}")
    private String fromMail;

    @Value("${TO_MAIL}")
    private String toMail;

    @Value("${USERNAME}")
    private String username;

    @Value("${PASSWORD}")
    private String password;

    private final Properties prop;

    public EmailSender() {
        prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "live.smtp.mailtrap.io");
        prop.put("mail.smtp.port", 587);
        prop.put("mail.smtp.ssl.trust", "live.smtp.mailtrap.io");
    }

    public void sendMail(String subject, String msg) throws Exception {

        Session session = getSession();

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromMail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toMail));
        message.setSubject(subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }

    private Session getSession() {
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        return session;
    }
}
