package com.minhduc.smartrestaurant.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.minhduc.smartrestaurant.domain.Category;
import com.minhduc.smartrestaurant.domain.dto.ResultPaginationDTO;
import com.minhduc.smartrestaurant.service.CategoryService;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/catagories")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        Category newcategory = categoryService.handleCreateCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(newcategory);
    }

    @GetMapping("/catagories/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable("id") long id) {
        Category category = categoryService.fetchCategoryById(id);
        return ResponseEntity.status(HttpStatus.OK).body(category);
    }

    @GetMapping("/catagories")
    public ResponseEntity<ResultPaginationDTO> getCatagories(@Filter Specification<Category> spec, Pageable pageable) {
        return ResponseEntity.ok(this.categoryService.fetchCategories(spec, pageable));
    }

    @PutMapping("/catagories")
    public ResponseEntity<Category> updateCategory(@RequestBody Category category) {
        Category updateCategory = categoryService.handleUpdateCategory(category);
        return ResponseEntity.status(HttpStatus.OK).body(updateCategory);
    }

    @DeleteMapping("/catagories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") long id) {
        categoryService.handleDeleteCategory(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
