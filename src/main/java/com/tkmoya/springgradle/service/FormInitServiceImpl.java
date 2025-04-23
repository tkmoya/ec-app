package com.tkmoya.springgradle.service;

import com.tkmoya.springgradle.entity.CategoryEntity;
import com.tkmoya.springgradle.model.CategoryModel;
import com.tkmoya.springgradle.repository.CategoryRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FormInitServiceImpl implements FormInitService {

    private final CategoryRepositoryImpl categoryRepositoryImpl;

    /**
     * カテゴリ選択肢を取得する
     *
     * @return カテゴリリスト
     */
    public List<CategoryModel> getCategoryChoices() {
        List<CategoryEntity> categoryEntityList = categoryRepositoryImpl.findAllCategory();
        List<CategoryModel> categoryList = new ArrayList<>();
        for (CategoryEntity entity : categoryEntityList) {
            // EntityからModelへ値を格納し、categoryListに詰めていく
            CategoryModel model = new CategoryModel();
            model.setCtgrId(entity.getCtgrId());
            model.setName(entity.getName());
            categoryList.add(model);
        }
        return categoryList;
    }
}