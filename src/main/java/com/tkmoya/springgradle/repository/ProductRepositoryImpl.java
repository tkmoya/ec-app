package com.tkmoya.springgradle.repository;

import com.tkmoya.springgradle.entity.ProductEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ProductRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    /**
     * 商品情報を取得するメソッド
     *
     * @param conditionMap 検索条件を格納したMap
     * @return 商品情報のリスト
     */
    @Override
    public List<ProductEntity> searchProduct(Map<String, String> conditionMap) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ONLINE_PRODUCT WHERE DELETE_FLG = '0' ");
        MapSqlParameterSource params = new MapSqlParameterSource();
        List<String> conditions = new ArrayList<>();

        if (conditionMap != null) {
            buildSearchConditions(conditionMap, params, conditions);
        }

        if (!conditions.isEmpty()) {
            sqlBuilder.append("AND ").append(String.join(" AND ", conditions));
        }

        return namedParameterJdbcTemplate.query(
                sqlBuilder.toString(),
                params,
                new BeanPropertyRowMapper<>(ProductEntity.class)
        );
    }

    private void buildSearchConditions(Map<String, String> conditionMap, MapSqlParameterSource params, List<String> conditions) {
        for (Map.Entry<String, String> entry : conditionMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value != null && !value.isEmpty()) {
                addCondition(key, value, params, conditions);
            }
        }
    }

    private void addCondition(String key, String value, MapSqlParameterSource params, List<String> conditions) {
        switch (key) {
            case "productCode":
                conditions.add("PRODUCT_CODE = :productCode");
                params.addValue("productCode", value);
                break;
            case "categoryId":
                conditions.add("CATEGORY_ID = :categoryId");
                params.addValue("categoryId", Integer.parseInt(value));
                break;
            case "productName":
                conditions.add("PRODUCT_NAME LIKE CONCAT('%', :productName, '%')");
                params.addValue("productName", value);
                break;
            case "maker":
                conditions.add("MAKER LIKE CONCAT('%', :maker, '%')");
                params.addValue("maker", value);
                break;
            case "unitPriceMin":
                conditions.add("UNIT_PRICE >= :unitPriceMin");
                params.addValue("unitPriceMin", Integer.parseInt(value));
                break;
            case "unitPriceMax":
                conditions.add("UNIT_PRICE <= :unitPriceMax");
                params.addValue("unitPriceMax", Integer.parseInt(value));
                break;
        }
    }
}