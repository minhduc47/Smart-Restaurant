package com.minhduc.smartrestaurant.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.minhduc.smartrestaurant.domain.Subscriber;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long>, JpaSpecificationExecutor<Subscriber> {

    boolean existsByEmail(String email);

    Optional<Subscriber> findByEmail(String email);

    @Query("SELECT DISTINCT s FROM Subscriber s JOIN s.categories c WHERE c.id = :categoryId AND s.active = true")
    List<Subscriber> findActiveSubscribersByCategoryId(@Param("categoryId") long categoryId);
}