package com.urbanfood.ecommerce.service;

import com.urbanfood.ecommerce.entity.LoginRequestDTO;
import com.urbanfood.ecommerce.entity.LoginResponseDTO;
import com.urbanfood.ecommerce.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        String storedPassword = customerRepository.getStoredPasswordByEmail(loginRequest.getEmail());

        if (storedPassword == null) {
            return new LoginResponseDTO(null, "User not found!");
        }

        if (storedPassword.equals(loginRequest.getPassword())) {
            Long customerId = customerRepository.getCustomerIdByEmail(loginRequest.getEmail());
            return new LoginResponseDTO(customerId, "Login successful");
        } else {
            return new LoginResponseDTO(null, "Invalid password!");
        }
    }


}
