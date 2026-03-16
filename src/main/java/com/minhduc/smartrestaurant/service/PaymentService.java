package com.minhduc.smartrestaurant.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhduc.smartrestaurant.domain.Order;
import com.minhduc.smartrestaurant.domain.Payment;
import com.minhduc.smartrestaurant.domain.RestaurantTable;
import com.minhduc.smartrestaurant.domain.request.ReqCheckoutPaymentDTO;
import com.minhduc.smartrestaurant.domain.response.ResPaymentDTO;
import com.minhduc.smartrestaurant.repository.OrderRepository;
import com.minhduc.smartrestaurant.repository.PaymentRepository;
import com.minhduc.smartrestaurant.util.constant.OrderStatusEnum;
import com.minhduc.smartrestaurant.util.constant.PaymentStatusEnum;
import com.minhduc.smartrestaurant.util.constant.TableEnum;
import com.minhduc.smartrestaurant.util.error.IdInvalidException;

@Service
public class PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public PaymentService(OrderRepository orderRepository, PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResPaymentDTO handleCheckout(ReqCheckoutPaymentDTO reqDTO) throws IdInvalidException {
        Order order = this.orderRepository.findById(reqDTO.getOrderId())
                .orElseThrow(
                        () -> new IdInvalidException("Đơn hàng với id = " + reqDTO.getOrderId() + " không tồn tại"));

        if (order.getStatus() == OrderStatusEnum.PAID || order.getPaymentStatus() == PaymentStatusEnum.PAID) {
            throw new IdInvalidException("Đơn hàng đã được thanh toán trước đó");
        }

        if (order.getOrderDetails() == null || order.getOrderDetails().isEmpty()) {
            throw new IdInvalidException("Đơn hàng không có món để thanh toán");
        }

        long recalculatedAmount = order.getOrderDetails().stream()
                .mapToLong(item -> item.getHistoricalPrice() * item.getQuantity())
                .sum();

        order.setTotalPrice(recalculatedAmount);
        order.setStatus(OrderStatusEnum.PAID);
        order.setPaymentStatus(PaymentStatusEnum.PAID);
        order.setPaymentMethod(reqDTO.getPaymentMethod());

        RestaurantTable table = order.getRestaurantTable();
        if (table != null) {
            table.setOccupied(TableEnum.AVAILABLE);
        }

        Payment payment = order.getPayment();
        if (payment == null) {
            payment = new Payment();
            payment.setOrder(order);
            order.setPayment(payment);
        }
        payment.setAmount(recalculatedAmount);
        payment.setPaymentMethod(reqDTO.getPaymentMethod());
        payment.setStatus(PaymentStatusEnum.PAID);
        payment.setTransactionRef(null);

        Order savedOrder = this.orderRepository.save(order);
        Payment savedPayment = this.paymentRepository.save(payment);

        ResPaymentDTO resDTO = new ResPaymentDTO();
        resDTO.setOrderId(savedOrder.getId());
        resDTO.setAmount(savedOrder.getTotalPrice());
        resDTO.setPaidAt(
                savedPayment.getUpdatedAt() != null ? savedPayment.getUpdatedAt() : savedPayment.getCreatedAt());
        resDTO.setTableStatus(savedOrder.getRestaurantTable() != null ? savedOrder.getRestaurantTable().getOccupied()
                : TableEnum.AVAILABLE);

        return resDTO;
    }
}