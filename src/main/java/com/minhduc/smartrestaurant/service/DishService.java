package com.minhduc.smartrestaurant.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.minhduc.smartrestaurant.domain.Category;
import com.minhduc.smartrestaurant.domain.Dish;
import com.minhduc.smartrestaurant.domain.response.ResultPaginationDTO;
import com.minhduc.smartrestaurant.repository.DishRepository;
import com.minhduc.smartrestaurant.util.error.IdInvalidException;

@Service
public class DishService {
    private final DishRepository dishRepository;
    private final CategoryService categoryService;

    public DishService(DishRepository dishRepository, CategoryService categoryService) {
        this.dishRepository = dishRepository;
        this.categoryService = categoryService;

    }

    public Dish handleCreateDish(Dish dish) throws IdInvalidException {
        if (dish.getCategory() != null) {
            Category category = this.categoryService.fetchCategoryById(dish.getCategory().getId());
            dish.setCategory(category);
        } else {
            throw new IdInvalidException("Category không được để trống");
        }
        return dishRepository.save(dish);
    }

    public Dish fetchDishById(long id) throws IdInvalidException {
        Optional<Dish> dish = dishRepository.findById(id);
        if (dish.isPresent()) {
            return dish.get();
        } else {
            throw new IdInvalidException("Dish với id = " + id + " không tồn tại");
        }
    }

    public ResultPaginationDTO fetchAllDishes(Specification<Dish> spec, Pageable pageable) {
        Page<Dish> pageDish = dishRepository.findAll(spec, pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageDish.getTotalPages());
        meta.setTotal(pageDish.getTotalElements());
        result.setMeta(meta);
        result.setResult(pageDish.getContent());
        return result;
    }

    public Dish handleUpdateDish(Dish dish) throws IdInvalidException {
        // Logic to handle dish update
        Dish existDish = fetchDishById(dish.getId());
        existDish.setName(dish.getName());
        existDish.setDescription(dish.getDescription());
        existDish.setPrice(dish.getPrice());
        existDish.setImage(dish.getImage());
        existDish.setActive(dish.isActive());
        if (dish.getCategory() != null) {
            Category category = this.categoryService.fetchCategoryById(dish.getCategory().getId());
            existDish.setCategory(category);
        } else {
            throw new IdInvalidException("Category không được để trống");
        }
        return existDish;
    }

    public void handleDeleteDish(long id) throws IdInvalidException {
        Dish currentDish = this.fetchDishById(id);
        currentDish.setActive(false);
        this.dishRepository.save(currentDish);
    }
}
