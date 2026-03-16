package com.minhduc.smartrestaurant.service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhduc.smartrestaurant.domain.Order;
import com.minhduc.smartrestaurant.domain.Payment;
import com.minhduc.smartrestaurant.domain.RestaurantTable;
import com.minhduc.smartrestaurant.domain.request.ReqCheckoutPaymentDTO;
import com.minhduc.smartrestaurant.domain.request.ReqCreateVNPayDTO;
import com.minhduc.smartrestaurant.domain.response.ResPaymentDTO;
import com.minhduc.smartrestaurant.repository.OrderRepository;
import com.minhduc.smartrestaurant.repository.PaymentRepository;
import com.minhduc.smartrestaurant.util.constant.OrderStatusEnum;
import com.minhduc.smartrestaurant.util.constant.PaymentMethodEnum;
import com.minhduc.smartrestaurant.util.constant.PaymentStatusEnum;
import com.minhduc.smartrestaurant.util.constant.TableEnum;
import com.minhduc.smartrestaurant.util.error.IdInvalidException;

@Service
public class PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final VNPayService vnPayService;

    public PaymentService(OrderRepository orderRepository, PaymentRepository paymentRepository,
            VNPayService vnPayService) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.vnPayService = vnPayService;
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

        Payment savedPayment = this.paymentRepository.save(payment);

        ResPaymentDTO resDTO = new ResPaymentDTO();
        resDTO.setOrderId(order.getId());
        resDTO.setAmount(order.getTotalPrice());
        resDTO.setPaidAt(
                savedPayment.getUpdatedAt() != null ? savedPayment.getUpdatedAt() : savedPayment.getCreatedAt());
        resDTO.setTableStatus(order.getRestaurantTable() != null ? order.getRestaurantTable().getOccupied()
                : TableEnum.AVAILABLE);

        return resDTO;
    }

    public Map<String, String> createVNPayPayment(ReqCreateVNPayDTO reqDTO, String ipAddress)
            throws IdInvalidException, UnsupportedEncodingException {
        Order order = this.orderRepository.findById(reqDTO.getOrderId())
                .orElseThrow(
                        () -> new IdInvalidException("Đơn hàng với id = " + reqDTO.getOrderId() + " không tồn tại"));

        if (order.getStatus() != OrderStatusEnum.PENDING) {
            throw new IdInvalidException("Chỉ có thể tạo link VNPay cho đơn hàng ở trạng thái PENDING");
        }

        if (order.getTotalPrice() <= 0) {
            throw new IdInvalidException("Số tiền đơn hàng không hợp lệ để thanh toán VNPay");
        }

        String paymentUrl = this.vnPayService.generateVNPayURL(order.getTotalPrice(), String.valueOf(order.getId()),
                ipAddress);

        Map<String, String> response = new HashMap<>();
        response.put("paymentUrl", paymentUrl);
        response.put("orderId", String.valueOf(order.getId()));
        return response;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> handleVNPayIpn(Map<String, String> params) throws UnsupportedEncodingException {
        Map<String, String> response = new HashMap<>();

        if (!this.vnPayService.validateSignature(params)) {
            response.put("RspCode", "97");
            response.put("Message", "Invalid signature");
            return response;
        }

        String txnRef = params.get("vnp_TxnRef");
        String vnpResponseCode = params.get("vnp_ResponseCode");
        String vnpTransactionNo = params.get("vnp_TransactionNo");
        String vnpAmount = params.get("vnp_Amount");

        if (txnRef == null || txnRef.isBlank()) {
            response.put("RspCode", "01");
            response.put("Message", "Order not found");
            return response;
        }

        Long orderId;
        try {
            orderId = Long.parseLong(txnRef);
        } catch (NumberFormatException e) {
            response.put("RspCode", "01");
            response.put("Message", "Order not found");
            return response;
        }

        Order order = this.orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            response.put("RspCode", "01");
            response.put("Message", "Order not found");
            return response;
        }

        if (order.getStatus() == OrderStatusEnum.PAID || order.getPaymentStatus() == PaymentStatusEnum.PAID) {
            response.put("RspCode", "02");
            response.put("Message", "Order already confirmed");
            return response;
        }

        long expectedAmount = order.getTotalPrice() * 100;
        long paidAmount;
        try {
            paidAmount = Long.parseLong(vnpAmount);
        } catch (Exception e) {
            response.put("RspCode", "04");
            response.put("Message", "Invalid amount");
            return response;
        }

        if (expectedAmount != paidAmount) {
            response.put("RspCode", "04");
            response.put("Message", "Invalid amount");
            return response;
        }

        if (!"00".equals(vnpResponseCode)) {
            response.put("RspCode", "00");
            response.put("Message", "Confirm success");
            return response;
        }

        order.setStatus(OrderStatusEnum.PAID);
        order.setPaymentStatus(PaymentStatusEnum.PAID);
        order.setPaymentMethod(PaymentMethodEnum.VNPAY);

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
        payment.setAmount(order.getTotalPrice());
        payment.setPaymentMethod(PaymentMethodEnum.VNPAY);
        payment.setStatus(PaymentStatusEnum.PAID);
        payment.setTransactionRef(vnpTransactionNo);

        this.paymentRepository.save(payment);

        response.put("RspCode", "00");
        response.put("Message", "Confirm success");
        return response;
    }

    public Map<String, Object> handleVNPayReturn(Map<String, String> params) throws UnsupportedEncodingException {
        Map<String, Object> response = new HashMap<>();

        boolean validSignature = this.vnPayService.validateSignature(params);
        String responseCode = params.get("vnp_ResponseCode");
        String txnRef = params.get("vnp_TxnRef");

        response.put("orderId", txnRef);
        response.put("transactionRef", params.get("vnp_TransactionNo"));
        response.put("responseCode", responseCode);
        response.put("validSignature", validSignature);

        boolean success = validSignature && "00".equals(responseCode);
        response.put("success", success);
        response.put("message", success ? "Thanh toán thành công" : "Thanh toán thất bại");

        return response;
    }
}