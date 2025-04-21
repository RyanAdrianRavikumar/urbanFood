package com.urbanfood.ecommerce.repository;

import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

import com.urbanfood.ecommerce.entity.Delivery;

@Repository
public interface DeliveryRepository extends CrudRepository<Delivery, Integer> {

    @Procedure(name = "add_delivery")
    void addDelivery(
            @Param("p_order_id") Integer orderId,
            @Param("p_delivery_date") java.sql.Date deliveryDate,
            @Param("p_delivery_status") String deliveryStatus
    );
}
