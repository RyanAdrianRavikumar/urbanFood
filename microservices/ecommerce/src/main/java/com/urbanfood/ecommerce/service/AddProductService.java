// ProductService.java
package com.urbanfood.ecommerce.service;

import com.urbanfood.ecommerce.repository.AddProductRepository;
import com.urbanfood.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Blob;

@Service
public class AddProductService {

    @Autowired
    private AddProductRepository AddproductRepository;

    public void addProduct(Integer supplierId, String name, String category, Double price, Integer stock, byte[] imageBytes) {
        try {
            AddproductRepository.addProduct(supplierId, name, category, price, stock, imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to add product via procedure", e);
        }
    }
}