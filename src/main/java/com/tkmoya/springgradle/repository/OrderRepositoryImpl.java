package com.tkmoya.springgradle.repository;

import com.tkmoya.springgradle.entity.OrderEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final NamedParameterJdbcTemplate namedParameterTemplate;

    private final JdbcTemplate jdbcTemplate;

    public OrderRepositoryImpl(NamedParameterJdbcTemplate namedParameterTemplate) {
        this.namedParameterTemplate = namedParameterTemplate;
        this.jdbcTemplate = namedParameterTemplate.getJdbcTemplate();
    }

    @Override
    public void InsertOrder(OrderEntity orderEntity) {
        String sql = "INSERT INTO ONLINE_ORDER(MEMBER_NO,TOTAL_MONEY,TOTAL_TAX,ORDER_DATE,COLLECT_NO,LAST_UPD_DATE) VALUES"
                + "(:memberNo,:totalMoney,:totalTax,NOW(),:collectNo,NOW())";
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("memberNo", orderEntity.getMemberNo());
        param.addValue("totalMoney", orderEntity.getTotalMoney());
        param.addValue("totalTax", orderEntity.getTotalTax());
        param.addValue("collectNo", String.valueOf(findMaxCollectNo()));
        this.namedParameterTemplate.update(sql, param);
    }

    @Override
    public void InsertOrderList(OrderEntity orderEntity) {
        String sql = "INSERT INTO ONLINE_ORDER_LIST(COLLECT_NO,PRODUCT_CODE,ORDER_COUNT,ORDER_PRICE) VALUES"
                + "(:collectNo,:productCode,:orderCount,:orderPrice)";
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("collectNo", String.valueOf(findMaxCollectNoNow()));
        param.addValue("productCode", orderEntity.getProductCode());
        param.addValue("orderCount", orderEntity.getOrderCount());
        param.addValue("orderPrice", orderEntity.getOrderPrice());
        this.namedParameterTemplate.update(sql, param);
    }


    @Override
    public void UpdateStock(OrderEntity orderEntity) {
        String sql = "UPDATE ONLINE_PRODUCT SET STOCK_COUNT=STOCK_COUNT-(:orderCount) WHERE PRODUCT_CODE = :productCode";
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("productCode", orderEntity.getProductCode());
        param.addValue("orderCount", orderEntity.getOrderCount());
        this.namedParameterTemplate.update(sql, param);
    }

    private Integer findMaxCollectNo() {
        String sql = "select MAX(CAST(COLLECT_NO as SIGNED))+1 from ONLINE_ORDER";
        return this.jdbcTemplate.queryForObject(sql, Integer.class);
    }

    private Integer findMaxCollectNoNow() {
        String sql = "select MAX(CAST(COLLECT_NO as SIGNED)) from ONLINE_ORDER";
        return this.jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Override
    public Integer getOrderNo() {
        String sql = "select MAX(CAST(ORDER_NO as SIGNED)) from ONLINE_ORDER";
        return this.jdbcTemplate.queryForObject(sql, Integer.class);
    }
}