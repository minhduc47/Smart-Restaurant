package com.minhduc.smartrestaurant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.minhduc.smartrestaurant.domain.Category;
import com.minhduc.smartrestaurant.repository.CategoryRepository;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category handleCreateCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category fetchCategoryById(long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            return category.get();
        } else {
            throw new RuntimeException("category not found");
        }
    }

    public List<Category> fetchAllCategory() {
        return categoryRepository.findAll();
    }

    public Category handleUpdateCategory(Category category) {
        // Logic to handle category update
        Category existCategory = fetchCategoryById(category.getId());
        existCategory.setName(category.getName());
        existCategory = this.categoryRepository.save(existCategory);
        return existCategory;
    }

    public void handleDeleteCategory(long id) {
        // Logic to handle category deletion
        this.categoryRepository.deleteById(id);
    }
}
