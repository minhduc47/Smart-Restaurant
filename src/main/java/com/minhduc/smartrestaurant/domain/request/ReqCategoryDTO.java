package com.minhduc.smartrestaurant.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCategoryDTO {
    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;
}
