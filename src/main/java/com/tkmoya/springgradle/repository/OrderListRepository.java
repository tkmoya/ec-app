package com.tkmoya.springgradle.repository;

import com.tkmoya.springgradle.entity.OrderListEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderListRepository {

    int InsertOrderList(OrderListEntity orderListEntity);

    List<OrderListEntity> getOrderListByOrDerNo(int orderNo);

}