package com.urbanfood.ecommerce.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Repository
public class OrderDetailsRepository {

    @Autowired
    private DataSource dataSource;

    public List<Map<String, Object>> getOrderDetailsByCustomerId(Long customerId) {
        List<Map<String, Object>> orderDetails = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{call getOrderDetailsByCustomerId(?, ?)}")) {

            stmt.setLong(1, customerId);
            stmt.registerOutParameter(2, Types.REF_CURSOR);

            stmt.execute();

            ResultSet rs = (ResultSet) stmt.getObject(2);

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("order_id", rs.getLong("order_id"));
                row.put("order_date", rs.getTimestamp("order_date"));
                row.put("status", rs.getString("status"));
                row.put("total_amount", rs.getDouble("total_amount"));
                row.put("product_name", rs.getString("product_name"));
                row.put("product_category", rs.getString("product_category"));
                row.put("quantity", rs.getInt("quantity"));
                row.put("subtotal", rs.getDouble("subtotal"));
                row.put("price", rs.getDouble("price"));
                orderDetails.add(row);
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderDetails;
    }
}

