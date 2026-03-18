package com.minhduc.smartrestaurant.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.minhduc.smartrestaurant.domain.PasswordResetToken;
import com.minhduc.smartrestaurant.domain.User;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUser(User user);

    @Modifying
    void deleteByUser(User user);

    @Modifying
    void deleteByExpiryDateBefore(Instant expiryDate);
}
