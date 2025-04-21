package com.urbanfood.ecommerce.service;

import com.urbanfood.ecommerce.repository.OrderDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrderDetailsService {

    @Autowired
    private OrderDetailsRepository orderProcedureRepository;

    public List<Map<String, Object>> getOrderDetails(Long customerId) {
        return orderProcedureRepository.getOrderDetailsByCustomerId(customerId);
    }
}

