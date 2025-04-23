package com.tkmoya.springgradle.service;

import com.tkmoya.springgradle.model.CategoryModel;

import java.util.List;

public interface FormInitService {

    /**
     * カテゴリリストを取得する
     *
     * @return カテゴリリスト
     */
    List<CategoryModel> getCategoryChoices();
}