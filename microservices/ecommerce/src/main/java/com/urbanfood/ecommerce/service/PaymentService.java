package com.urbanfood.ecommerce.service;

import com.urbanfood.ecommerce.entity.PaymentRequestDTO;
import com.urbanfood.ecommerce.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public String handlePayment(PaymentRequestDTO dto) {
        return paymentRepository.processPayment(dto);
    }
}
