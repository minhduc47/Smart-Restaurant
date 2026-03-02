package com.minhduc.smartrestaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.minhduc.smartrestaurant.domain.RestaurantTable;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

}
