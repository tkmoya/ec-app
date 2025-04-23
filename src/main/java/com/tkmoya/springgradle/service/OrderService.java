package com.tkmoya.springgradle.service;

import com.tkmoya.springgradle.model.OrderModel;
import com.tkmoya.springgradle.model.SearchProductResultModel;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.TreeMap;

public interface OrderService {

    /**
     * 注文処理
     */
    @Transactional(rollbackForClassName = "Exception")
    boolean OrderExecute(OrderModel order, TreeMap<String, SearchProductResultModel> orderProductMap)
            throws SQLException;

    int getOrderNo();
}