package com.foodapp.emailNotifications.controller;

import com.foodapp.emailNotifications.service.SendEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:3001", "http://localhost:3000"})
@RestController
public class EmailController {

    @Autowired
    private SendEmailService sendEmailService;

    @PostMapping("/Suppliers/{supplierId}/LowStockEmail")
    public ResponseEntity<String> notifyLowStock(@PathVariable int supplierId) {
        sendEmailService.sendLowStockNotification(supplierId);
        return ResponseEntity.ok("Low stock notification sent to supplier ID: " + supplierId);
    }
}