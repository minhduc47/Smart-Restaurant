package com.minhduc.smartrestaurant.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.minhduc.smartrestaurant.util.constant.TableEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "restaurant_tables")
@Getter
@Setter
public class RestaurantTable extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "Tên bàn không được để trống")
    private String name;
    private String qrToken;
    @Enumerated(EnumType.STRING)
    private TableEnum occupied;
    @OneToMany(mappedBy = "restaurantTable", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Order> orders;
}