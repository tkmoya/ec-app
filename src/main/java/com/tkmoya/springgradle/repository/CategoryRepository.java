package com.tkmoya.springgradle.repository;

import com.tkmoya.springgradle.entity.CategoryEntity;

import java.util.List;

public interface CategoryRepository {
    List<CategoryEntity> findAllCategory();
}