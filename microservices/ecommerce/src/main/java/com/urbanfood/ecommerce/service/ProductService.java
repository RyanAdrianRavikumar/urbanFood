package com.urbanfood.ecommerce.service;

import com.urbanfood.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Map<String, Object> getProductDetails(int productId){
        return productRepository.getProductDetails(productId);
    }

    public List<Map<String, Object>> getAllProducts(){
        return productRepository.getAllProducts();
    }

    public String updateProductDetails(int productId, String productName, String productCategory, double price, int availableStock){
        return productRepository.updateProductDetails(productId, productName, productCategory, price, availableStock);
    }

    public Map<String, Object> getProductDetailsByName(String productName) {
        return productRepository.getProductDetailsByName(productName);
    }

    public String deleteProductById(int productId) {
        return productRepository.deleteProductById(productId);
    }

}
