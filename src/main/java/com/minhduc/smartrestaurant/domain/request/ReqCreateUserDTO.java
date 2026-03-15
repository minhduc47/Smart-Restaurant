package com.minhduc.smartrestaurant.domain.request;

import com.minhduc.smartrestaurant.util.constant.GenderEnum;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCreateUserDTO {
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Password không được để trống")
    @Size(min = 6, message = "Password phải có ít nhất 6 ký tự")
    private String password;

    @NotBlank(message = "Name không được để trống")
    private String name;

    private int age;
    private GenderEnum gender;
    private String address;

    @NotNull(message = "roleId không được để trống")
    private Long roleId;
}