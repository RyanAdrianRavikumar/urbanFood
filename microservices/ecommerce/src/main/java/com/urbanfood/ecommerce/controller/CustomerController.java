package com.urbanfood.ecommerce.controller;

import com.urbanfood.ecommerce.entity.LoginRequestDTO;
import com.urbanfood.ecommerce.entity.LoginResponseDTO;
import com.urbanfood.ecommerce.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("customer/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = customerService.login(loginRequest);

        if (response.getCustomerId() != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }
}
