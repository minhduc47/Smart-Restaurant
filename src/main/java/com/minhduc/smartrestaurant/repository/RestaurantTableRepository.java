package com.minhduc.smartrestaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.minhduc.smartrestaurant.domain.RestaurantTable;

@Repository
public interface RestaurantTableRepository
        extends JpaRepository<RestaurantTable, Long>, JpaSpecificationExecutor<RestaurantTable> {

}
