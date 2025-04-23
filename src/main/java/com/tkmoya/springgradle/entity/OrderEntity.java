package com.tkmoya.springgradle.entity;

import lombok.Data;

import java.sql.Date;

@Data
public class OrderEntity {
    private String orderNo;
    private String memberNo;
    private String totalMoney;
    private String totalTax;
    private Date orderDate;
    private String collectNo;
    private Date lastUpdDate;

    private String listNo;
    private String productCode;
    private String orderCount;
    private String orderPrice;
}