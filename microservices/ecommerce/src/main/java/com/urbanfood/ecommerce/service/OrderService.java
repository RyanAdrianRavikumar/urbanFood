package com.urbanfood.ecommerce.service;

import com.urbanfood.ecommerce.entity.OrderItem;
import com.urbanfood.ecommerce.entity.OrderRequestDTO;
import com.urbanfood.ecommerce.repository.DeliveryRepository;
import com.urbanfood.ecommerce.repository.OrderItemRepository;
import com.urbanfood.ecommerce.repository.OrderProcedureRepository;
import com.urbanfood.ecommerce.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private OrderProcedureRepository orderProcedureRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    public int placeOrder(OrderRequestDTO requestDTO) throws Exception {
        Integer[] productIds = requestDTO.getProductIds().toArray(new Integer[0]);
        Integer[] quantities = requestDTO.getQuantities().toArray(new Integer[0]);

        // Place the order and get the generated order ID
        int orderId = orderProcedureRepository.placeOrder(requestDTO.getCustomerId(), productIds, quantities);

        // Call the delivery procedure
        deliveryRepository.addDelivery(orderId, Date.valueOf(LocalDate.now()), "In Progress");

        return orderId;
    }
}
