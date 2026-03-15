package com.minhduc.smartrestaurant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springdoc.core.annotations.ParameterObject;

import com.minhduc.smartrestaurant.domain.Category;
import com.minhduc.smartrestaurant.domain.request.ReqCategoryDTO;
import com.minhduc.smartrestaurant.domain.response.ResCategoryDTO;
import com.minhduc.smartrestaurant.domain.response.ResultPaginationDTO;
import com.minhduc.smartrestaurant.service.CategoryService;
import com.minhduc.smartrestaurant.util.annotation.ApiMessage;
import com.minhduc.smartrestaurant.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import io.swagger.v3.oas.annotations.Parameter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/categories")
    @ApiMessage("Create a new category")
    public ResponseEntity<ResCategoryDTO> createCategory(@Valid @RequestBody ReqCategoryDTO reqCategoryDTO) {
        ResCategoryDTO newcategory = categoryService.handleCreateCategory(reqCategoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newcategory);
    }

    @GetMapping("/categories/{id}")
    @ApiMessage("Fetch category by id")
    public ResponseEntity<Category> getCategory(@PathVariable("id") long id) throws IdInvalidException {
        Category category = categoryService.fetchCategoryById(id);
        return ResponseEntity.status(HttpStatus.OK).body(category);
    }

    @GetMapping("/categories")
    @ApiMessage("Fetch all categories with pagination")
    public ResponseEntity<ResultPaginationDTO> getCategories(
            @Parameter(name = "filter", description = "Query filter (VD: name ~ 'duck')") @Filter Specification<Category> spec,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(this.categoryService.fetchCategories(spec, pageable));
    }

    @PutMapping("/categories")
    @ApiMessage("Update a category")
    public ResponseEntity<Category> updateCategory(@RequestBody Category category) throws IdInvalidException {
        Category updateCategory = categoryService.handleUpdateCategory(category);
        return ResponseEntity.status(HttpStatus.OK).body(updateCategory);
    }

    @DeleteMapping("/categories/{id}")
    @ApiMessage("Delete a category")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") long id) throws IdInvalidException {
        categoryService.handleDeleteCategory(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
