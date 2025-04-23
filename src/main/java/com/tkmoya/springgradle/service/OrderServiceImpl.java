package com.tkmoya.springgradle.service;

import com.tkmoya.springgradle.entity.OrderEntity;
import com.tkmoya.springgradle.model.OrderModel;
import com.tkmoya.springgradle.model.SearchProductResultModel;
import com.tkmoya.springgradle.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
//    private final OrderListRepositoryImpl orderListRepositoryImpl;

    /**
     * 注文処理
     */
    @Transactional(rollbackForClassName = "Exception")
    public boolean OrderExecute(OrderModel order, TreeMap<String, SearchProductResultModel> orderProductMap)
            throws SQLException {

        // OrderEntityに値収納
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setMemberNo(order.getMemberNo());
        orderEntity.setTotalMoney(order.getTotalMoney());
        orderEntity.setTotalTax(order.getTotalTax());
        orderEntity.setCollectNo(order.getCollectNo());


        orderEntity.setProductCode(order.getProductCode());
        orderEntity.setOrderCount(order.getOrderCount());
        orderEntity.setOrderPrice(order.getOrderPrice());
        orderRepository.InsertOrder(orderEntity);

        for (Map.Entry<String, SearchProductResultModel> orderelement : orderProductMap.entrySet()) {
            SearchProductResultModel element = orderelement.getValue();
            orderEntity.setProductCode(element.getProductCode());
            orderEntity.setOrderCount(element.getProductCnt());
            orderEntity.setOrderPrice(element.getUnitPrice());
            orderRepository.InsertOrderList(orderEntity);
        }

        for (Map.Entry<String, SearchProductResultModel> orderelement : orderProductMap.entrySet()) {
            SearchProductResultModel element = orderelement.getValue();
            orderEntity.setProductCode(element.getProductCode());
            orderEntity.setOrderCount(element.getProductCnt());
            orderRepository.UpdateStock(orderEntity);
        }

        return true;
    }

    @Override
    public int getOrderNo() {
        return orderRepository.getOrderNo();
    }
}