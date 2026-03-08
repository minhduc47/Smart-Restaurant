package com.minhduc.smartrestaurant.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

import com.minhduc.smartrestaurant.domain.Dish;
import com.minhduc.smartrestaurant.domain.request.DishRequestDTO;
import com.minhduc.smartrestaurant.domain.response.DishResponseDTO;
import com.minhduc.smartrestaurant.domain.response.ResultPaginationDTO;
import com.minhduc.smartrestaurant.service.DishService;
import com.minhduc.smartrestaurant.util.annotation.ApiMessage;
import com.minhduc.smartrestaurant.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class DishController {
    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @PostMapping("/dishes")
    @ApiMessage("Create a new dish")
    public ResponseEntity<DishResponseDTO> createDish(@Valid @RequestBody DishRequestDTO reqDTO)
            throws IdInvalidException {
        Dish newDish = dishService.handleCreateDish(reqDTO);
        DishResponseDTO resDTO = dishService.convertToDishResponseDTO(newDish);
        return ResponseEntity.status(HttpStatus.CREATED).body(resDTO);
    }

    @GetMapping("/dishes/{id}")
    @ApiMessage("Fetch dish by id")
    public ResponseEntity<Dish> getDish(@PathVariable("id") long id) throws IdInvalidException {
        Dish dish = dishService.fetchDishById(id);
        return ResponseEntity.status(HttpStatus.OK).body(dish);
    }

    @GetMapping("/dishes")
    @ApiMessage("Fetch all dishes with pagination")
    public ResponseEntity<ResultPaginationDTO> getAllDishes(@Filter Specification<Dish> spec, Pageable pageable) {
        return ResponseEntity.ok(this.dishService.fetchAllDishes(spec, pageable));
    }

    @PutMapping("/dishes")
    @ApiMessage("Update a dish")
    public ResponseEntity<Dish> updateDish(@Valid @RequestBody Dish dish) throws IdInvalidException {
        Dish updatedDish = dishService.handleUpdateDish(dish);
        return ResponseEntity.status(HttpStatus.OK).body(updatedDish);
    }

    @DeleteMapping("/dishes/{id}")
    @ApiMessage("Delete a dish")
    public ResponseEntity<Void> deleteDish(@PathVariable("id") long id) throws IdInvalidException {
        dishService.handleDeleteDish(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
