package com.urbanfood.ecommerce.controller;


import com.urbanfood.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.Map;

@RestController
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping(path = "/products/{productId}")
    public Map<String, Object> getProductDetails(@PathVariable int productId){
        return productService.getProductDetails(productId);
    }

    @GetMapping(path = "/products")
    public List<Map<String, Object>> getAllProducts(){
        return productService.getAllProducts();
    }
}
