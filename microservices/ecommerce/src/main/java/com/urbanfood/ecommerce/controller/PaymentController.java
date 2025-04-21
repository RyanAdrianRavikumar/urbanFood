package com.urbanfood.ecommerce.controller;

import com.urbanfood.ecommerce.entity.PaymentRequestDTO;
import com.urbanfood.ecommerce.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/customers/{customerId}/orders/{orderId}/payments")
    public ResponseEntity<String> processPayment(@PathVariable Long customerId, @PathVariable Long orderId, @RequestBody PaymentRequestDTO dto) {

        // Set the IDs from path to the DTO to ensure consistency
        dto.setCustomerId(customerId);
        dto.setOrderId(orderId);

        String result = paymentService.handlePayment(dto);

        if (result.startsWith("Success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(500).body(result);
        }
    }
}
