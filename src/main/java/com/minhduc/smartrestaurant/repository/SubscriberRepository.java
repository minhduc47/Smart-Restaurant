package com.minhduc.smartrestaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.minhduc.smartrestaurant.domain.Subscriber;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long>, JpaSpecificationExecutor<Subscriber> {

    boolean existsByEmail(String email);

    List<Subscriber> findByCategoriesId(long categoryId);
}