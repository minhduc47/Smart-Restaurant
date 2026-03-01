package com.minhduc.smartrestaurant.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dishes")
@Getter
@Setter
public class Dish extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "Tên món ăn không được để trống")
    private String name;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
    @Min(value = 0, message = "Giá món ăn phải lớn hơn hoặc bằng 0")
    private long price;
    private String image;
    boolean active;
    @ManyToOne
    @NotNull(message = "Món ăn phải thuộc về một danh mục")
    @JoinColumn(name = "category_id")
    private Category category;
}
