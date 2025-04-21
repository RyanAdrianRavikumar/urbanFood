package com.urbanfood.ecommerce.service;

import com.urbanfood.ecommerce.entity.ProductLowStockDTO;
import com.urbanfood.ecommerce.repository.ProductLowStockRepository;
import com.urbanfood.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductLowStockRepository productLowStockRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String LOW_STOCK_NOTIFICATION_URL = "http://localhost:8086/Suppliers/{supplierId}/LowStockEmail";


    public Map<String, Object> getProductDetails(int productId){
        return productRepository.getProductDetails(productId);
    }

    public List<Map<String, Object>> getAllProducts() {
        List<Map<String, Object>> products = productRepository.getAllProducts();

        for (Map<String, Object> product : products) {
            Object stockObj = product.get("stock");
            Object supplierIdObj = product.get("supplierId");

            if (stockObj != null && supplierIdObj != null) {
                int stock = ((Number) stockObj).intValue();
                int supplierId = ((Number) supplierIdObj).intValue();

                if (stock < 10) {
                    try {
                        restTemplate.postForEntity(LOW_STOCK_NOTIFICATION_URL, null, String.class, supplierId);
                    } catch (Exception e) {
                        System.err.println("Failed to notify low stock for supplier ID: " + supplierId);
                    }
                }
            } else {
                System.err.println("Missing 'stock' or 'supplierId' for a product: " + product);
            }
        }

        return products;
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

    public List<ProductLowStockDTO> getOutOfStockProducts() {
        return productLowStockRepository.getOutOfStockProducts();
    }
}
