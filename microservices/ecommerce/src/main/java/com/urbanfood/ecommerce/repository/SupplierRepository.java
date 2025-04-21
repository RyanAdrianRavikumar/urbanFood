package com.urbanfood.ecommerce.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

@Repository
public class SupplierRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public SupplierCredentials fetchCredentialsByEmail(String email) {
        System.out.println("[DEBUG] Attempting to fetch supplier credentials for email: " + email);

        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("get_supplier_credentials_by_email");

            query.registerStoredProcedureParameter("p_supplier_email", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_supplier_id", Long.class, ParameterMode.OUT);
            query.registerStoredProcedureParameter("p_stored_password", String.class, ParameterMode.OUT);

            query.setParameter("p_supplier_email", email.toLowerCase().trim()); // ensure lowercase and trim

            query.execute();

            Long supplierId = (Long) query.getOutputParameterValue("p_supplier_id");
            String storedPassword = (String) query.getOutputParameterValue("p_stored_password");

            System.out.println("[DEBUG] Stored Procedure Output - supplierId: " + supplierId + ", password: " + storedPassword);

            if (supplierId != null && storedPassword != null) {
                System.out.println("[DEBUG] Successfully fetched supplier credentials.");
                return new SupplierCredentials(supplierId, storedPassword);
            } else {
                System.out.println("[DEBUG] Supplier credentials not found or null values returned.");
            }

        } catch (Exception e) {
            System.out.println("[ERROR] Exception occurred while fetching supplier credentials:");
            e.printStackTrace();
        }

        return null; // If exception or null values
    }

    public static class SupplierCredentials {
        private final Long supplierId;
        private final String storedPassword;

        public SupplierCredentials(Long supplierId, String storedPassword) {
            this.supplierId = supplierId;
            this.storedPassword = storedPassword;
        }

        public Long getSupplierId() {
            return supplierId;
        }

        public String getStoredPassword() {
            return storedPassword;
        }
    }
}
