package com.urbanfood.ecommerce.entity;

import jakarta.persistence.*;
import org.springframework.boot.autoconfigure.web.WebProperties;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

@Entity
@Table(name = "PRODUCTS")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private int productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_category")
    private String productCategory;

    @Column(name = "price")
    private Double price;

    @Column(name = "available_stock")
    private int availableStock;

    @Lob
    @Column(name = "product_image")
    private Blob productImage;

    public String getProductImage() {
        if (productImage != null) {
            try {
                int blobLength = (int) productImage.length();
                byte[] blobAsBytes = productImage.getBytes(1, blobLength);
                return Base64.getEncoder().encodeToString(blobAsBytes);
            } catch (SQLException e) {
                e.printStackTrace(); // Handle the exception appropriately
                return null; // Or throw an exception
            }
        }
        return null;
    }

    public void setProductImage(Blob productImage) {
        this.productImage = productImage;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(int availableStock) {
        this.availableStock = availableStock;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
