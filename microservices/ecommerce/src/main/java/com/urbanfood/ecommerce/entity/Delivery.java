package com.urbanfood.ecommerce.entity;
import jakarta.persistence.*;

import java.sql.Date;

@Entity
@NamedStoredProcedureQuery(
        name = "add_delivery",
        procedureName = "add_delivery",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_order_id", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_delivery_date", type = Date.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_delivery_status", type = String.class)
        }
)
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deliveryId;

    private Integer orderId;
    private Date deliveryDate;
    private String deliveryStatus;

    public Integer getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Integer deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    // Getters and setters
}

