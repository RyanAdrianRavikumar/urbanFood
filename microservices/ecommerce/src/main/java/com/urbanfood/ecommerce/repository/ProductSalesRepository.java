package com.urbanfood.ecommerce.repository;

import com.urbanfood.ecommerce.entity.BestSellingProductDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class ProductSalesRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SimpleJdbcCall simpleJdbcCall;

    @PostConstruct
    private void init() {
        simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("get_best_selling_products")
                .returningResultSet("p_result", new BestSellingProductRowMapper());
    }

    public List<BestSellingProductDTO> fetchBestSellingProducts() {
        Map<String, Object> result = simpleJdbcCall.execute();
        Object cursor = result.get("p_result");

        if (cursor instanceof List<?>) {
            return (List<BestSellingProductDTO>) cursor;
        }

        return Collections.emptyList();
    }

    private static class BestSellingProductRowMapper implements RowMapper<BestSellingProductDTO> {
        @Override
        public BestSellingProductDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            BestSellingProductDTO dto = new BestSellingProductDTO();
            dto.setProductName(rs.getString("product_name"));
            dto.setTotalSold(rs.getInt("total_sold"));
            return dto;
        }
    }
}
