package com.tkmoya.springgradle.entity;

import lombok.Data;

@Data
public class OrderListEntity {
    private String listNo;
    private String collectNo;
    private String productCode;
    private String orderCount;
    private String orderPrice;
}