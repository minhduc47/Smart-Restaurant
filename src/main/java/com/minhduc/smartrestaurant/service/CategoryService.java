package com.minhduc.smartrestaurant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.minhduc.smartrestaurant.domain.Category;
import com.minhduc.smartrestaurant.domain.request.ReqCategoryDTO;
import com.minhduc.smartrestaurant.domain.request.ReqUpdateCategoryDTO;
import com.minhduc.smartrestaurant.domain.response.ResCategoryDTO;
import com.minhduc.smartrestaurant.domain.response.ResultPaginationDTO;
import com.minhduc.smartrestaurant.repository.CategoryRepository;
import com.minhduc.smartrestaurant.util.error.IdInvalidException;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public ResCategoryDTO handleCreateCategory(ReqCategoryDTO reqCategoryDTO) {
        Category category = new Category();
        category.setName(reqCategoryDTO.getName());

        Category savedCategory = categoryRepository.save(category);
        return this.convertToResCategoryDTO(savedCategory);
    }

    public ResCategoryDTO convertToResCategoryDTO(Category category) {
        ResCategoryDTO resCategoryDTO = new ResCategoryDTO();
        resCategoryDTO.setId(category.getId());
        resCategoryDTO.setName(category.getName());
        resCategoryDTO.setCreatedAt(category.getCreatedAt());
        resCategoryDTO.setCreatedBy(category.getCreatedBy());
        return resCategoryDTO;
    }

    public Category fetchCategoryById(long id) throws IdInvalidException {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            return category.get();
        } else {
            throw new IdInvalidException("Category với id = " + id + " không tồn tại");
        }
    }

    public ResultPaginationDTO fetchCategories(Specification<Category> spec, Pageable pageable) {
        Page<Category> pageCategory = categoryRepository.findAll(spec, pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageCategory.getTotalPages());
        meta.setTotal(pageCategory.getTotalElements());
        result.setMeta(meta);
        result.setResult(pageCategory.getContent());
        return result;
    }

    public Category handleUpdateCategory(ReqUpdateCategoryDTO reqCategoryDTO) throws IdInvalidException {
        Category existCategory = fetchCategoryById(reqCategoryDTO.getId());
        existCategory.setName(reqCategoryDTO.getName());
        existCategory = this.categoryRepository.save(existCategory);
        return existCategory;
    }

    public void handleDeleteCategory(long id) throws IdInvalidException {
        Category currentCategory = this.fetchCategoryById(id);
        this.categoryRepository.delete(currentCategory);
    }
}
