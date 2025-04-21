package com.urbanfood.ecommerce.repository;

import com.urbanfood.ecommerce.entity.PaymentRequestDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public String processPayment(PaymentRequestDTO dto) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("process_payment_update");

        query.registerStoredProcedureParameter("p_order_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_customer_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_method", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_amount", Double.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_status", String.class, ParameterMode.OUT);

        query.setParameter("p_order_id", dto.getOrderId());
        query.setParameter("p_customer_id", dto.getCustomerId());
        query.setParameter("p_method", dto.getPaymentMethod());
        query.setParameter("p_amount", dto.getAmount());

        query.execute();
        return (String) query.getOutputParameterValue("p_status");
    }
}
