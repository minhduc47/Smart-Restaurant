package com.minhduc.smartrestaurant.domain;

import com.minhduc.smartrestaurant.util.constant.GenderEnum;
import com.minhduc.smartrestaurant.util.constant.PaymentEnum;
import com.minhduc.smartrestaurant.util.constant.PaymentStatusEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment extends BaseEntity {
    @Id
    private long id;
    private long amount;
    @Enumerated(EnumType.STRING)
    private PaymentEnum paymentMethod;
    @Enumerated(EnumType.STRING)
    private PaymentStatusEnum status;
    private String transactionRef;
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "order_id")
    private Order order;

}
