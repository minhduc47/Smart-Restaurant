package com.minhduc.smartrestaurant.domain.request;

import java.util.List;

import com.minhduc.smartrestaurant.util.constant.OrderEnum;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCreateOrderDTO {

    private Long tableId;

    @NotNull(message = "Loại đơn hàng không được để trống")
    private OrderEnum orderType;

    private String note;

    @NotEmpty(message = "Đơn hàng phải có ít nhất 1 món")
    private List<OrderItem> items;

    @Getter
    @Setter
    public static class OrderItem {
        @NotNull(message = "ID món ăn không được để trống")
        private Long dishId;

        @Min(value = 1, message = "Số lượng phải lớn hơn 0")
        private int quantity;

        private String note;
    }
}