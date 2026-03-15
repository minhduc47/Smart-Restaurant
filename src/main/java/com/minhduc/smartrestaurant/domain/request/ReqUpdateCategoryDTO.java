package com.minhduc.smartrestaurant.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateCategoryDTO {
    @NotNull(message = "id không được để trống")
    private Long id;

    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;
}