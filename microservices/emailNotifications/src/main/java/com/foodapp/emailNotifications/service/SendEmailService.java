package com.foodapp.emailNotifications.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SendEmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${spring.mail.username}")
    private String fromEmailId;

    private final String SUPPLIER_SERVICE_URL = "http://localhost:8080/Suppliers/";

    public void sendEmail(String recipient, String body, String subject) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmailId);
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }

    public void sendLowStockNotification(int supplierId) {
        String emailUrl = SUPPLIER_SERVICE_URL + supplierId + "/email";
        String supplierEmail = restTemplate.getForObject(emailUrl, String.class);

        if (supplierEmail == null || supplierEmail.isEmpty()) {
            throw new RuntimeException("Supplier email not found for ID: " + supplierId);
        }

        String subject = "Urgent: Inventory Level Low";
        String body = "Dear Valued Supplier,\n\n" +
                "This is to inform you that your products in our inventory are running low.\n" +
                "Please arrange for restocking at your earliest convenience.\n\n" +
                "Best regards,\n" +
                "Inventory Management System";

        sendEmail(supplierEmail, body, subject);
    }
}