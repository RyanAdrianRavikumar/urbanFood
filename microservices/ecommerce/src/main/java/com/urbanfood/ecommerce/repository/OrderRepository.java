package com.urbanfood.ecommerce.repository;

import com.urbanfood.ecommerce.entity.Order;
import com.urbanfood.ecommerce.entity.OrderItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;
import oracle.jdbc.OracleConnection;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Repository
public class OrderRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final DataSource dataSource;

    // Constructor injection for DataSource (HikariCP)
    public OrderRepository(EntityManager entityManager, DataSource dataSource) {
        this.entityManager = entityManager;
        this.dataSource = dataSource;
    }

    @Transactional
    public Long placeOrder(Long customerId, List<Integer> productIds, List<Integer> quantities) {
        Connection conn = null;
        try {
            // Get the connection from DataSource (HikariCP) directly
            conn = dataSource.getConnection();

            // Cast to OracleConnection
            if (conn instanceof OracleConnection) {
                OracleConnection oracleConn = (OracleConnection) conn;

                // Convert productIds to Oracle ARRAY
                ArrayDescriptor productArrayDesc = ArrayDescriptor.createDescriptor("SYS.ODCINUMBERLIST", oracleConn);
                ARRAY productArray = new ARRAY(productArrayDesc, oracleConn, productIds.toArray(new Integer[0]));

                // Convert quantities to Oracle ARRAY
                ArrayDescriptor quantityArrayDesc = ArrayDescriptor.createDescriptor("SYS.ODCINUMBERLIST", oracleConn);
                ARRAY quantityArray = new ARRAY(quantityArrayDesc, oracleConn, quantities.toArray(new Integer[0]));

                // Call the stored procedure
                StoredProcedureQuery query = entityManager.createStoredProcedureQuery("place_order");

                query.registerStoredProcedureParameter("p_customer_id", Long.class, ParameterMode.IN);
                query.registerStoredProcedureParameter("p_product_ids", ARRAY.class, ParameterMode.IN);
                query.registerStoredProcedureParameter("p_quantities", ARRAY.class, ParameterMode.IN);
                query.registerStoredProcedureParameter("p_order_id", Long.class, ParameterMode.OUT);

                query.setParameter("p_customer_id", customerId);
                query.setParameter("p_product_ids", productArray);
                query.setParameter("p_quantities", quantityArray);

                query.execute();

                // Return the output order_id
                return (Long) query.getOutputParameterValue("p_order_id");
            } else {
                throw new SQLException("Failed to get Oracle connection");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing stored procedure", e);
        } finally {
            // Close connection after use
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // Handle connection close failure if needed
            }
        }
    }


}
