package com.minhduc.smartrestaurant.domain.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateDishDTO {
    @NotNull(message = "id không được để trống")
    private Long id;

    @NotBlank(message = "Tên món ăn không được để trống")
    private String name;

    private String description;

    @Min(value = 0, message = "Giá món ăn phải lớn hơn hoặc bằng 0")
    private long price;

    private String image;
    private boolean active;

    private Long categoryId;
}