package com.minhduc.smartrestaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.minhduc.smartrestaurant.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
