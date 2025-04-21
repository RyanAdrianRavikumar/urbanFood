package com.urbanfood.ecommerce.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

@Repository
public class ProductRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getProductDetails(int productId) {
        CallableStatement statement = null;
        Map<String, Object> result = new HashMap<>();

        try {
            Connection connection = jdbcTemplate.getDataSource().getConnection();
            statement = connection.prepareCall("{call GetProductDetails(?, ?, ?, ?, ?, ?)}");

            statement.setInt(1, productId);

            // Register output parameters
            statement.registerOutParameter(2, Types.VARCHAR);
            statement.registerOutParameter(3, Types.VARCHAR);
            statement.registerOutParameter(4, Types.NUMERIC);
            statement.registerOutParameter(5, Types.NUMERIC);
            statement.registerOutParameter(6, Types.BLOB); // Register BLOB output

            // Execute stored procedure
            statement.execute();

            // Retrieve output values
            String productName = statement.getString(2);
            String productCategory = statement.getString(3);
            BigDecimal price = statement.getBigDecimal(4);
            int stock = statement.getInt(5);
            Blob imageBlob = statement.getBlob(6); // Get BLOB

            // Convert BLOB to Base64 if it exists
            String base64Image = null;
            if (imageBlob != null) {
                byte[] imageBytes = imageBlob.getBytes(1, (int) imageBlob.length());
                base64Image = Base64.getEncoder().encodeToString(imageBytes);
            }

            // Populate result map
            result.put("product_name", productName);
            result.put("product_category", productCategory);
            result.put("price", price);
            result.put("stock", stock);
            result.put("product_image", base64Image);

        } catch (SQLException e) {
            e.printStackTrace();
            result.put("error", "Product not found");
        } finally {
            try {
                if (statement != null) statement.close();
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
                product.put("supplier_id", rs.getInt("supplier_id"));

                // Fetch the image data (assuming the column name is 'product_image')
                byte[] imageBytes = rs.getBytes("product_image");

                // Convert image bytes to a Base64 string if required
                if (imageBytes != null) {
                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                    product.put("product_image", base64Image); // Add the Base64 image to the map
                } else {
                    product.put("product_image", null); // If no image, add null
                }

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



    public String updateProductDetails(int productId, String productName, String productCategory, double price, int availableStock) {
        String sql = "{call UpdateProductById(?, ?, ?, ?, ?)}";

        return jdbcTemplate.execute(connection -> {
            CallableStatement statement = connection.prepareCall(sql);
            statement.setInt(1, productId);
            statement.setString(2, productName);
            statement.setString(3, productCategory);
            statement.setDouble(4, price);
            statement.setInt(5, availableStock);
            return statement;
        }, (CallableStatement cs) -> {
            try {
                cs.execute();
                return "Product details updated successfully";
            } catch (SQLException e) {
                e.printStackTrace();
                return "Error updating product details";
            }
        });
    }

    public Map<String, Object> getProductDetailsByName (String productName)
    {
        Map<String, Object> result = new HashMap<>();
        CallableStatement statement = null;

        try {
            Connection connection = jdbcTemplate.getDataSource().getConnection();
            statement = connection.prepareCall("{call GetProductDetailsByName(?, ?, ?, ?, ?, ?)}");

            statement.setString(1, productName);

            // Register output parameters
            statement.registerOutParameter(2, Types.NUMERIC);
            statement.registerOutParameter(3, Types.VARCHAR);
            statement.registerOutParameter(4, Types.NUMERIC);
            statement.registerOutParameter(5, Types.NUMERIC);
            statement.registerOutParameter(6, Types.BLOB); // Register BLOB output

            // Execute stored procedure
            statement.execute();

            // Retrieve output values
            int productId = statement.getInt(2);         // product_id
            String productCategory = statement.getString(3);           // category
            BigDecimal price = statement.getBigDecimal(4);             // price
            int stock = statement.getInt(5);                           // stock
            Blob imageBlob = statement.getBlob(6);                     // image

            // Convert BLOB to Base64 if it exists
            String base64Image = null;
            if (imageBlob != null) {
                byte[] imageBytes = imageBlob.getBytes(1, (int) imageBlob.length());
                base64Image = Base64.getEncoder().encodeToString(imageBytes);
            }

            // Populate result map
            result.put("product_name", productName);
            result.put("product_category", productCategory);
            result.put("price", price);
            result.put("stock", stock);
            result.put("product_image", base64Image);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public String deleteProductById(int productId) {
        String status = "";
        CallableStatement statement = null;

        try {
            Connection connection = jdbcTemplate.getDataSource().getConnection();
            statement = connection.prepareCall("{call DeleteProductById(?, ?)}");

            statement.setInt(1, productId);
            statement.registerOutParameter(2, Types.VARCHAR); // status message used to return if it worked or not

            statement.execute();
            status = statement.getString(2);

        } catch (SQLException e) {
            e.printStackTrace();
            status = "Error occurred while trying to delete product.";
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return status;
    }

}
