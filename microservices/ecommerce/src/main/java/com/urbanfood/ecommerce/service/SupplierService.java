package com.urbanfood.ecommerce.service;

import com.urbanfood.ecommerce.entity.LoginRequestDTO;
import com.urbanfood.ecommerce.entity.SupplierLoginResponseDTO;
import com.urbanfood.ecommerce.repository.SupplierEmailRepository;
import com.urbanfood.ecommerce.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierEmailRepository supplierEmailRepository;

    public SupplierLoginResponseDTO login(LoginRequestDTO loginRequest) {
        SupplierRepository.SupplierCredentials credentials = supplierRepository.fetchCredentialsByEmail(loginRequest.getEmail());

        if (credentials == null) {
            return new SupplierLoginResponseDTO(null, "User not found!");
        }

        if (credentials.getStoredPassword().equals(loginRequest.getPassword())) {
            return new SupplierLoginResponseDTO(credentials.getSupplierId(), "Login successful");
        } else {
            return new SupplierLoginResponseDTO(null, "Invalid password!");
        }
    }

    public String getSupplierEmailById(int supplierId) {
        String email = supplierEmailRepository.findEmailBySupplierId(supplierId);
        if (email == null) {
            throw new RuntimeException("Supplier not found or has no email with id: " + supplierId);
        }
        return email;
    }
}

