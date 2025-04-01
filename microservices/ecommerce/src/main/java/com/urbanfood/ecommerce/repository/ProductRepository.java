package com.urbanfood.ecommerce.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Repository
public class ProductRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getProductDetails(int productId) {
        CallableStatement statement = null;
        Map<String, Object> result = new HashMap<>();

        try {
            // Log the input productId
            System.out.println("Received productId: " + productId);

            // Open a connection to the database and prepare the statement
            statement = jdbcTemplate.getDataSource().getConnection().prepareCall("{call GetProductDetails(?, ?, ?, ?, ?)}");

            // Set the input parameter
            statement.setInt(1, productId);

            // Register output parameters
            statement.registerOutParameter(2, Types.VARCHAR);
            statement.registerOutParameter(3, Types.VARCHAR);
            statement.registerOutParameter(4, Types.NUMERIC);
            statement.registerOutParameter(5, Types.NUMERIC);

            // Execute the stored procedure
            statement.execute();

            // Log the outputs for debugging
            String productName = statement.getString(2);
            String productCategory = statement.getString(3);
            BigDecimal price = statement.getBigDecimal(4);
            int stock = statement.getInt(5);

            System.out.println("Product Name: " + productName);
            System.out.println("Product Category: " + productCategory);
            System.out.println("Price: " + price);
            System.out.println("Stock: " + stock);

            // Populate the result map
            result.put("product_name", productName);
            result.put("product_category", productCategory);
            result.put("price", price);
            result.put("stock", stock);

        } catch (SQLException e) {
            // Log the error if the procedure call fails
            e.printStackTrace();
            result.put("product_name", "Not Found");
            result.put("product_category", "Not Found");
            result.put("price", 0);
            result.put("stock", 0);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    // Method to get all products
    public List<Map<String, Object>> getAllProducts() {
        CallableStatement statement = null;
        List<Map<String, Object>> products = new ArrayList<>();

        try {
            // Log the call for fetching all products
            System.out.println("Fetching all products.");

            // Open a connection to the database and prepare the statement
            statement = jdbcTemplate.getDataSource().getConnection().prepareCall("{call GetAllProducts(?)}");

            // Register output parameter for the cursor
            statement.registerOutParameter(1, Types.REF_CURSOR);

            // Execute the stored procedure
            statement.execute();

            // Retrieve the result set from the cursor
            ResultSet rs = (ResultSet) statement.getObject(1);

            // Map the result set to a list of product details
            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("product_id", rs.getInt("product_id"));
                product.put("product_name", rs.getString("product_name"));
                product.put("product_category", rs.getString("product_category"));
                product.put("price", rs.getBigDecimal("price"));
                product.put("available_stock", rs.getInt("available_stock"));
                products.add(product);
            }

            // Close the result set
            rs.close();

        } catch (SQLException e) {
            // Log the error if the procedure call fails
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return products;
    }
}
