package com.example.mylibrary.service;

import com.example.mylibrary.model.entity.Category;
import com.example.mylibrary.model.enums.CategoryName;

public interface CategoryService {
    void seedCategories();

    Category getCategory(CategoryName category);
}
