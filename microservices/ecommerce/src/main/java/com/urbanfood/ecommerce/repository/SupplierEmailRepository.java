package com.urbanfood.ecommerce.repository;

import com.urbanfood.ecommerce.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierEmailRepository extends JpaRepository<Supplier, Integer> {

    @Query("SELECT s.supplierEmail FROM Supplier s WHERE s.supplierId = :supplierId")
    String findEmailBySupplierId(@Param("supplierId") int supplierId);

    // Keep this if you need full supplier object elsewhere
    Supplier findBySupplierId(int supplierId);
}
