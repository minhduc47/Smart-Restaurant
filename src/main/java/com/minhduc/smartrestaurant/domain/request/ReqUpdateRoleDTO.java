package com.minhduc.smartrestaurant.domain.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateRoleDTO {
    @NotNull(message = "id không được để trống")
    private Long id;

    @NotBlank(message = "Tên vai trò không được để trống")
    private String name;

    private String description;
    private boolean active;
    private List<Long> permissionIds;
}