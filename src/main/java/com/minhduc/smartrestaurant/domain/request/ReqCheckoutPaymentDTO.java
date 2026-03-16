package com.minhduc.smartrestaurant.domain.request;

import com.minhduc.smartrestaurant.util.constant.PaymentMethodEnum;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCheckoutPaymentDTO {
    @NotNull(message = "orderId không được để trống")
    private Long orderId;

    @NotNull(message = "paymentMethod không được để trống")
    private PaymentMethodEnum paymentMethod;
}