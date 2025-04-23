package com.tkmoya.springgradle.service;

import com.tkmoya.springgradle.model.SearchProductModel;
import com.tkmoya.springgradle.model.SearchProductResultModel;

import java.util.List;

public interface ProductService {
    /**
     * 検索条件から商品情報を得る
     *
     * @param searchProduct
     * @return
     */
    List<List<SearchProductResultModel>> searchProduct(SearchProductModel searchProduct);

    /**
     * ProductCodeから商品情報を得る
     *
     * @param selectProductCode
     * @return
     */
    SearchProductResultModel searchProductByCode(String selectProductCode);

    /**
     * 検索結果を最大10件のページにかくのうする
     *
     * @param list
     * @return
     */
    List<List<SearchProductResultModel>> CreatePageList(List<SearchProductResultModel> list);

}