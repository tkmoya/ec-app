package com.tkmoya.springgradle.repository;

import com.tkmoya.springgradle.entity.ProductEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ProductRepository {
    /**
     * 商品情報を取得するメソッド
     *
     * @param conditionMap 検索条件を格納したMap
     * @return 商品情報のリスト
     */
    List<ProductEntity> searchProduct(Map<String, String> conditionMap);
}