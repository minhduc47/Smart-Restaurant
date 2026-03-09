package com.minhduc.smartrestaurant.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqTableDTO {
    @NotBlank(message = "Tên bàn không được để trống")
    private String name;

    private String qrToken;
}
