package com.urbanfood.ecommerce.controller;

import com.urbanfood.ecommerce.entity.OrderRequestDTO;
import com.urbanfood.ecommerce.service.OrderDetailsService;
import com.urbanfood.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class OrderController {

    private final OrderService orderService;

    @Autowired
    OrderDetailsService orderDetailsService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/customer/{customerId}/order")
    public ResponseEntity<String> placeOrder(
            @PathVariable int customerId,
            @RequestBody OrderRequestDTO orderRequest) {

        try {
            orderRequest.setCustomerId(customerId);
            int orderId = orderService.placeOrder(orderRequest);
            return ResponseEntity.ok("Order placed successfully! Order ID: " + orderId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Order placement failed: " + e.getMessage());
        }
    }

    @GetMapping("/customers/{customerId}/orders")
    public List<Map<String, Object>> getOrderDetails(@PathVariable Long customerId) {
        return orderDetailsService.getOrderDetails(customerId);
    }

}
