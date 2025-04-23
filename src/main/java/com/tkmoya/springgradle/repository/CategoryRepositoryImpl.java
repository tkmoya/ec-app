package com.tkmoya.springgradle.repository;

import com.tkmoya.springgradle.entity.CategoryEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private final NamedParameterJdbcTemplate template;

    public CategoryRepositoryImpl(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    @Override
    public List<CategoryEntity> findAllCategory() {
        return this.template.query("select * from ONLINE_CATEGORY", new BeanPropertyRowMapper<>(CategoryEntity.class));
    }
}