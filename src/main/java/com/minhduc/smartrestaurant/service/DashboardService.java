package com.minhduc.smartrestaurant.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.minhduc.smartrestaurant.domain.User;
import com.minhduc.smartrestaurant.domain.response.ResDashboardDTO;
import com.minhduc.smartrestaurant.repository.OrderDetailRepository;
import com.minhduc.smartrestaurant.repository.OrderRepository;
import com.minhduc.smartrestaurant.repository.UserRepository;

@Service
public class DashboardService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;

    public DashboardService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository,
            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.userRepository = userRepository;
    }

    public ResDashboardDTO getDashboardStats() {
        ResDashboardDTO res = new ResDashboardDTO();

        long totalRevenue = defaultLong(orderRepository.getTotalRevenueFromPaidOrders());

        List<OrderRepository.OrderStatusCountProjection> statusStats = orderRepository.countOrdersByStatus();
        long totalOrders = statusStats.stream()
                .mapToLong(item -> defaultLong(item.getTotal()))
                .sum();

        long paidOrders = statusStats.stream()
                .filter(item -> "PAID".equalsIgnoreCase(item.getStatus()))
                .mapToLong(item -> defaultLong(item.getTotal()))
                .sum();

        long pendingOrders = statusStats.stream()
                .filter(item -> "PENDING".equalsIgnoreCase(item.getStatus()))
                .mapToLong(item -> defaultLong(item.getTotal()))
                .sum();

        Map<String, Long> revenueByMethod = new LinkedHashMap<>();
        revenueByMethod.put("CASH", 0L);
        revenueByMethod.put("VNPAY", 0L);

        orderRepository.getRevenueByPaymentMethod().forEach(item -> {
            if (item.getPaymentMethod() != null) {
                revenueByMethod.put(item.getPaymentMethod(), defaultLong(item.getRevenue()));
            }
        });

        List<ResDashboardDTO.TopSellingDishDTO> topSellingDishes = orderDetailRepository.findTopSellingDishes().stream()
                .map(item -> {
                    ResDashboardDTO.TopSellingDishDTO dishDTO = new ResDashboardDTO.TopSellingDishDTO();
                    dishDTO.setDishId(defaultLong(item.getDishId()));
                    dishDTO.setDishName(item.getDishName());
                    dishDTO.setTotalQuantity(defaultLong(item.getTotalQuantity()));
                    return dishDTO;
                })
                .toList();

        res.setTotalRevenue(totalRevenue);
        res.setTotalOrders(totalOrders);
        res.setPaidOrders(paidOrders);
        res.setPendingOrders(pendingOrders);
        res.setRevenueByMethod(revenueByMethod);
        res.setTopSellingDishes(topSellingDishes);
        return res;
    }

    public boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String email = authentication.getName();
        if (email == null || email.isBlank() || "anonymousUser".equals(email)) {
            return false;
        }

        User currentUser = userRepository.findByEmail(email);
        return currentUser != null
                && currentUser.getRole() != null
                && currentUser.getRole().getName() != null
                && "ADMIN".equalsIgnoreCase(currentUser.getRole().getName());
    }

    private long defaultLong(Long value) {
        return value == null ? 0L : value;
    }
}