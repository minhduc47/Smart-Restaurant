package com.minhduc.smartrestaurant.domain.response;

import java.time.Instant;

import com.minhduc.smartrestaurant.util.constant.TableEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResPaymentDTO {
    private long orderId;
    private long amount;
    private Instant paidAt;
    private TableEnum tableStatus;
}