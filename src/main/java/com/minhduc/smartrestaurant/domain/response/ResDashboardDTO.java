package com.minhduc.smartrestaurant.domain.response;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResDashboardDTO {
    private long totalRevenue;
    private long totalOrders;
    private long paidOrders;
    private long pendingOrders;
    private Map<String, Long> revenueByMethod;
    private List<TopSellingDishDTO> topSellingDishes;

    @Getter
    @Setter
    public static class TopSellingDishDTO {
        private long dishId;
        private String dishName;
        private long totalQuantity;
    }
}