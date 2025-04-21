package com.urbanfood.ecommerce.repository;
import jakarta.annotation.PostConstruct;
import oracle.jdbc.OracleConnection;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Types;
import java.util.Map;

@Repository
public class OrderProcedureRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private DataSource dataSource;

    @PostConstruct
    private void init() {
        this.dataSource = jdbcTemplate.getDataSource();
    }

    public int placeOrder(int customerId, Integer[] productIds, Integer[] quantities) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            OracleConnection oracleConnection = conn.unwrap(OracleConnection.class);

            // Create Oracle-compatible arrays
            ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor("SYS.ODCINUMBERLIST", oracleConnection);
            ARRAY productIdArray = new ARRAY(descriptor, oracleConnection, productIds);
            ARRAY quantityArray = new ARRAY(descriptor, oracleConnection, quantities);

            // Call the place_order procedure
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("place_order")
                    .declareParameters(
                            new SqlOutParameter("p_order_id", Types.INTEGER)
                    );

            Map<String, Object> result = jdbcCall.execute(
                    Map.of(
                            "p_customer_id", customerId,
                            "p_product_ids", productIdArray,
                            "p_quantities", quantityArray
                    )
            );

            Number orderId = (Number) result.get("p_order_id");
            if (orderId == null) throw new RuntimeException("Order procedure did not return an order ID.");
            int generatedOrderId = orderId.intValue();

            // Retrieve total amount for the order
            Double totalAmount = jdbcTemplate.queryForObject(
                    "SELECT total_amount FROM Orders WHERE order_id = ?",
                    new Object[]{generatedOrderId},
                    Double.class
            );

            // Call the process_payment procedure
            SimpleJdbcCall paymentCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("process_payment");

            paymentCall.execute(
                    Map.of(
                            "p_order_id", generatedOrderId,
                            "p_customer_id", customerId,
                            "p_payment_method", "Cash", // You can make this dynamic
                            "p_amount", totalAmount,
                            "p_status", "Pending"
                    )
            );

            return generatedOrderId;
        }
    }

}