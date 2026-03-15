package com.minhduc.smartrestaurant.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.minhduc.smartrestaurant.domain.Category;
import com.minhduc.smartrestaurant.domain.Dish;
import com.minhduc.smartrestaurant.domain.request.DishRequestDTO;
import com.minhduc.smartrestaurant.domain.request.ReqUpdateDishDTO;
import com.minhduc.smartrestaurant.domain.response.DishResponseDTO;
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

    public Dish handleCreateDish(DishRequestDTO reqDTO) throws IdInvalidException {
        Category category = this.categoryService.fetchCategoryById(reqDTO.getCategoryId());

        Dish dish = new Dish();
        dish.setName(reqDTO.getName());
        dish.setDescription(reqDTO.getDescription());
        dish.setPrice(reqDTO.getPrice());
        dish.setImage(reqDTO.getImage());
        dish.setCategory(category);
        dish.setActive(true);

        return dishRepository.save(dish);
    }

    public DishResponseDTO convertToDishResponseDTO(Dish dish) {
        DishResponseDTO resDTO = new DishResponseDTO();
        resDTO.setId(dish.getId());
        resDTO.setName(dish.getName());
        resDTO.setDescription(dish.getDescription());
        resDTO.setPrice(dish.getPrice());
        resDTO.setImage(dish.getImage());
        resDTO.setActive(dish.isActive());

        if (dish.getCategory() != null) {
            resDTO.setCategoryId(dish.getCategory().getId());
            resDTO.setCategoryName(dish.getCategory().getName());
        }

        return resDTO;
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

    public Dish handleUpdateDish(ReqUpdateDishDTO reqDTO) throws IdInvalidException {
        Dish existDish = fetchDishById(reqDTO.getId());
        existDish.setName(reqDTO.getName());
        existDish.setDescription(reqDTO.getDescription());
        existDish.setPrice(reqDTO.getPrice());
        existDish.setImage(reqDTO.getImage());
        existDish.setActive(reqDTO.isActive());

        if (reqDTO.getCategoryId() != null) {
            Category category = this.categoryService.fetchCategoryById(reqDTO.getCategoryId());
            existDish.setCategory(category);
        }

        return this.dishRepository.save(existDish);
    }

    public void handleDeleteDish(long id) throws IdInvalidException {
        Dish currentDish = this.fetchDishById(id);
        currentDish.setActive(false);
        this.dishRepository.save(currentDish);
    }
}
