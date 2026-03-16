package com.minhduc.smartrestaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.minhduc.smartrestaurant.domain.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

}