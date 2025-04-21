package com.urbanfood.ecommerce.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Long getCustomerIdByEmail(String email) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("get_customer_credentials_by_email");

        // Register parameters
        query.registerStoredProcedureParameter("p_customer_email", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_customer_id", Long.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("p_stored_password", String.class, ParameterMode.OUT);

        query.setParameter("p_customer_email", email);
        query.execute();

        Long customerId = (Long) query.getOutputParameterValue("p_customer_id");
        String storedPassword = (String) query.getOutputParameterValue("p_stored_password");

        if (customerId == null || storedPassword == null) {
            return null;
        }

        return customerId;
    }

    public String getStoredPasswordByEmail(String email) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("get_customer_credentials_by_email");

        query.registerStoredProcedureParameter("p_customer_email", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_customer_id", Long.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("p_stored_password", String.class, ParameterMode.OUT);

        query.setParameter("p_customer_email", email);
        query.execute();

        return (String) query.getOutputParameterValue("p_stored_password");
    }
}
