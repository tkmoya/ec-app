package com.tkmoya.springgradle.repository;

import com.tkmoya.springgradle.entity.OrderListEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderListRepositoryImpl implements OrderListRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public OrderListRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public int InsertOrderList(OrderListEntity orderListEntity) {
        String sql = "INSERT INTO ONLINE_ORDER_LIST(COLLECT_NO,PRODUCT_CODE,ORDER_COUNT,ORDER_PRICE) VALUES"
                + "(:collectNo,:productCode,:orderCount,:orderPrice)";
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("collectNo", orderListEntity.getCollectNo());
        param.addValue("productCode", orderListEntity.getProductCode());
        param.addValue("orderCount", orderListEntity.getOrderCount());
        param.addValue("orderPrice", orderListEntity.getOrderPrice());
        return this.namedParameterJdbcTemplate.update(sql, param);
    }

    @Override
    public List<OrderListEntity> getOrderListByOrDerNo(int orderNo) {
        String sql = "select * from ORDER_LIST where ORDER_NUM = :orderNo";
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("orderNo", orderNo);
        return this.namedParameterJdbcTemplate.query(sql, param, new BeanPropertyRowMapper<>(OrderListEntity.class));
    }

}