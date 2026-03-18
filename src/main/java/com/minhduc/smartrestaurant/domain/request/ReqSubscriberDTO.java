package com.minhduc.smartrestaurant.domain.request;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqSubscriberDTO {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Tên không được để trống")
    private String name;

    @NotEmpty(message = "categoryIds không được để trống")
    private List<Long> categoryIds;
}
