package com.example.mylibrary.init;

import com.example.mylibrary.interceptor.MaintenanceInterceptor;
import com.example.mylibrary.service.CategoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CategorySeeder implements CommandLineRunner {

    private CategoryService categoryService;
    private MaintenanceInterceptor maintenanceInterceptor;

    public CategorySeeder(CategoryService categoryService, MaintenanceInterceptor maintenanceInterceptor) {
        this.categoryService = categoryService;
        this.maintenanceInterceptor = maintenanceInterceptor;
    }

    @Override
    public void run(String... args) throws Exception {
        this.categoryService.seedCategories();
    }


}
