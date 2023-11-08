package com.example.mylibrary.service.impl;

import com.example.mylibrary.model.entity.Category;
import com.example.mylibrary.model.enums.CategoryName;
import com.example.mylibrary.repository.CategoryRepository;
import com.example.mylibrary.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void seedCategories() {
        if (this.categoryRepository.count() == 0) {
            List<Category> categories = Arrays.stream(CategoryName.values())
                    .map(Category::new)
                    .collect(Collectors.toList());
            this.categoryRepository.saveAll(categories);
        }
    }
}
