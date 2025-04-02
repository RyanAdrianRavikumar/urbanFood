package com.urbanfood.ecommerce.service;

import com.urbanfood.ecommerce.entity.OrderItem;
import com.urbanfood.ecommerce.repository.OrderItemRepository;
import com.urbanfood.ecommerce.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Transactional
    public int placeOrder(int customerId, double totalAmount, List<Map<String, Object>> orderItems) {
        // Call stored procedure to place order
        int orderId = orderRepository.placeOrder(customerId, totalAmount);

        // Add order items using stored procedure
        for (Map<String, Object> item : orderItems) {
            int productId = (int) item.get("productId");
            int quantity = (int) item.get("quantity");

            orderItemRepository.addOrderItem(orderId, productId, quantity);
        }

        return orderId;
    }
}
