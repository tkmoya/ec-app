package com.tkmoya.springgradle.entity;

import lombok.Data;

@Data
public class ProductEntity {
    private String productCode;
    private String categoryId;
    private String productName;
    private String maker;
    private String stockCount;
    private String unitPrice;
    private String pictureName;
    private String memo;
    private String deleteFlg;
}