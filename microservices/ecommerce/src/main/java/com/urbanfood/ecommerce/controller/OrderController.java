package com.urbanfood.ecommerce.controller;

import com.urbanfood.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping(path = "/customers/{customerId}/orders")
    public String placeOrder(
            @PathVariable int customerId,
            @RequestBody Map<String, Object> orderData) {

        double totalAmount = (double) orderData.get("totalAmount");
        List<Map<String, Object>> orderItems = (List<Map<String, Object>>) orderData.get("orderItems");

        int orderId = orderService.placeOrder(customerId, totalAmount, orderItems);

        return "Order placed successfully with ID: " + orderId;
    }
}
