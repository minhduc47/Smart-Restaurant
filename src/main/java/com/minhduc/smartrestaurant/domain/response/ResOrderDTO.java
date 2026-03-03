package com.minhduc.smartrestaurant.domain.response;

import java.time.Instant;

import com.minhduc.smartrestaurant.util.constant.OrderEnum;
import com.minhduc.smartrestaurant.util.constant.OrderStatusEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResOrderDTO {
    private long id;
    private long totalPrice;
    private OrderStatusEnum status;
    private OrderEnum orderType;
    private String note;
    private Instant createdAt;

    private Long tableId;

}