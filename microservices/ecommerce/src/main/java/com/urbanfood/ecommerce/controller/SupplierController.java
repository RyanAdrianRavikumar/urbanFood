package com.urbanfood.ecommerce.controller;

import com.urbanfood.ecommerce.entity.LoginRequestDTO;
import com.urbanfood.ecommerce.entity.LoginResponseDTO;
import com.urbanfood.ecommerce.entity.SupplierLoginResponseDTO;
import com.urbanfood.ecommerce.service.CustomerService;
import com.urbanfood.ecommerce.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @PostMapping("Suppliers/login")
    public ResponseEntity<SupplierLoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        SupplierLoginResponseDTO response = supplierService.login(loginRequest);

        if (response.getSupplierId() != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }

    @GetMapping("Suppliers/{supplierId}/email")
    public ResponseEntity<String> getSupplierEmail(@PathVariable int supplierId) {
        try {
            String email = supplierService.getSupplierEmailById(supplierId);
            return ResponseEntity.ok(email);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
