package com.minhduc.smartrestaurant.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.minhduc.smartrestaurant.util.constant.OrderEnum;
import com.minhduc.smartrestaurant.util.constant.OrderStatusEnum;
import com.minhduc.smartrestaurant.util.constant.PaymentMethodEnum;
import com.minhduc.smartrestaurant.util.constant.PaymentStatusEnum;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Min(value = 0, message = "Tổng tiền không được âm")
    private long totalPrice;
    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;
    @Enumerated(EnumType.STRING)
    private PaymentStatusEnum paymentStatus;
    @Enumerated(EnumType.STRING)
    private PaymentMethodEnum paymentMethod;
    @Enumerated(EnumType.STRING)
    private OrderEnum orderType;
    private String note;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "table_id")
    private RestaurantTable restaurantTable;
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<OrderDetail> orderDetails;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonIgnore
    private Payment payment;
}
