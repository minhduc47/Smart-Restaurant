package com.minhduc.smartrestaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.minhduc.smartrestaurant.domain.Catagory;

@Repository
public interface CatagoryRepository extends JpaRepository<Catagory, Long> {

}
