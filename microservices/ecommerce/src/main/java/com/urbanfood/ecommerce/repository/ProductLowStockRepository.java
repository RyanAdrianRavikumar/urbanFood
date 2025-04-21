package com.urbanfood.ecommerce.repository;


import com.urbanfood.ecommerce.entity.ProductLowStockDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductLowStockRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<ProductLowStockDTO> getOutOfStockProducts() {
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("get_out_of_stock_products");

        query.registerStoredProcedureParameter("p_product_details", void.class, ParameterMode.REF_CURSOR);

        query.execute();

        List<Object[]> results = query.getResultList();
        List<ProductLowStockDTO> products = new ArrayList<>();

        for (Object[] row : results) {
            ProductLowStockDTO dto = new ProductLowStockDTO();
            dto.setProductId(((Number) row[0]).longValue());
            dto.setSupplierId(((Number) row[1]).longValue());
            dto.setProductName((String) row[2]);
            dto.setProductCategory((String) row[3]);
            dto.setPrice(((Number) row[4]).doubleValue());
            dto.setAvailableStock(((Number) row[5]).intValue());

            products.add(dto);
        }

        return products;
    }
}
