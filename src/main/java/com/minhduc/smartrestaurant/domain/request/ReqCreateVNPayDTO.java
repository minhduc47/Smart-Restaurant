package com.minhduc.smartrestaurant.domain.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCreateVNPayDTO {
    @NotNull(message = "orderId không được để trống")
    private Long orderId;
}
