package com.urbanfood.ecommerce.repository;

import com.urbanfood.ecommerce.entity.Order;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Transactional
    @Procedure(procedureName = "place_order")
    int placeOrder(@Param("p_customer_id") int customerId,
                   @Param("p_total_amount") double totalAmount);

}
