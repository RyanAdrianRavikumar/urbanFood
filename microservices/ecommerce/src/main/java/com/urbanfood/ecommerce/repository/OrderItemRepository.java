package com.urbanfood.ecommerce.repository;

import com.urbanfood.ecommerce.entity.OrderItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    @Transactional
    @Procedure(procedureName = "add_order_item")
    void addOrderItem(@Param("p_order_id") int orderId,
                      @Param("p_product_id") int productId,
                      @Param("p_quantity") int quantity);
}
