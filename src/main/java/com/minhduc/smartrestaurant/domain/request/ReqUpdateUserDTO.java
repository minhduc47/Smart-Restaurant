package com.minhduc.smartrestaurant.domain.request;

import com.minhduc.smartrestaurant.util.constant.GenderEnum;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateUserDTO {
    @NotNull(message = "id không được để trống")
    private Long id;

    private String name;
    private int age;
    private GenderEnum gender;
    private String address;
    private Long roleId;
}