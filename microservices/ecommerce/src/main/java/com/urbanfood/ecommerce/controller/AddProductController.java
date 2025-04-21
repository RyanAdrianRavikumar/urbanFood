package com.urbanfood.ecommerce.controller;

import com.urbanfood.ecommerce.service.AddProductService;
import com.urbanfood.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AddProductController {

    @Autowired
    private AddProductService AddproductService;

    @PostMapping("products/add")
    public ResponseEntity<String> addProduct(
            @RequestParam Integer supplierId,
            @RequestParam String name,
            @RequestParam String category,
            @RequestParam Double price,
            @RequestParam Integer stock,
            @RequestParam MultipartFile image
    ) {
        try {
            byte[] imageBytes = image.getBytes();
            AddproductService.addProduct(supplierId, name, category, price, stock, imageBytes);
            return ResponseEntity.ok("Product added successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error adding product: " + e.getMessage());
        }
    }
}