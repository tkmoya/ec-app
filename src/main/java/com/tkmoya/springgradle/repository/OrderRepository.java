package com.tkmoya.springgradle.repository;

import com.tkmoya.springgradle.entity.OrderEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository {
    void InsertOrder(OrderEntity orderEntity);

    void InsertOrderList(OrderEntity orderEntity);

    void UpdateStock(OrderEntity orderEntity);

    Integer getOrderNo();
}