package com.minhduc.smartrestaurant.domain.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqRoleDTO {
    private Long id;

    @NotBlank(message = "Tên vai trò không được để trống")
    private String name;

    private String description;
    private boolean active;
    private List<Long> permissionIds;
}