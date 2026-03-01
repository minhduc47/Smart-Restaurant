package com.minhduc.smartrestaurant.domain;

import java.time.Instant;

import com.minhduc.smartrestaurant.util.SecurityUtil;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    @Column(updatable = false)
    private Instant createdAt;
    private Instant updatedAt;

    @Column(updatable = false)
    private String createdBy;
    private String updatedBy;

    @PrePersist
    public void handleBeforeCreate() {
        String userLogin = SecurityUtil.getCurrentUserLogin().orElse("system");
        this.createdBy = userLogin;
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        String userLogin = SecurityUtil.getCurrentUserLogin().orElse("system");
        this.updatedBy = userLogin;
        this.updatedAt = Instant.now();
    }
}