package com.urbanfood.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import com.urbanfood.ecommerce.entity.Product;

public interface AddProductRepository extends JpaRepository<Product, Integer> {

    @Procedure(procedureName = "add_product")   // Using procedureName instead of name
    void addProduct(
            @Param("p_supplier_id") Integer supplierId,
            @Param("p_name") String name,
            @Param("p_category") String category,
            @Param("p_price") Double price,
            @Param("p_stock") Integer stock,
            @Param("p_image") byte[] image
    );
}
