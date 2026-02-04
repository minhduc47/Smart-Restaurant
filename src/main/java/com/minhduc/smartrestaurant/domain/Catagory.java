package com.minhduc.smartrestaurant.domain;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "catagories")
@Getter
@Setter
public class Catagory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private boolean isActive;
    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}
