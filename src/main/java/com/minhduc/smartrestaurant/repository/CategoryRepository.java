package com.minhduc.smartrestaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.minhduc.smartrestaurant.domain.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}
