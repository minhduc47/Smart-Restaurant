package com.minhduc.smartrestaurant.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhduc.smartrestaurant.domain.Dish;
import com.minhduc.smartrestaurant.domain.Order;
import com.minhduc.smartrestaurant.domain.OrderDetail;
import com.minhduc.smartrestaurant.domain.RestaurantTable;
import com.minhduc.smartrestaurant.domain.request.ReqCreateOrderDTO;
import com.minhduc.smartrestaurant.domain.response.ResOrderDTO;
import com.minhduc.smartrestaurant.domain.response.ResultPaginationDTO;
import com.minhduc.smartrestaurant.repository.DishRepository;
import com.minhduc.smartrestaurant.repository.OrderRepository;
import com.minhduc.smartrestaurant.repository.RestaurantTableRepository;
import com.minhduc.smartrestaurant.util.constant.OrderEnum;
import com.minhduc.smartrestaurant.util.constant.OrderStatusEnum;
import com.minhduc.smartrestaurant.util.constant.PaymentStatusEnum;
import com.minhduc.smartrestaurant.util.constant.TableEnum;
import com.minhduc.smartrestaurant.util.error.IdInvalidException;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final DishRepository dishRepository;
    private final RestaurantTableRepository restaurantTableRepository;

    public OrderService(OrderRepository orderRepository, DishRepository dishRepository,
            RestaurantTableRepository restaurantTableRepository) {
        this.orderRepository = orderRepository;
        this.dishRepository = dishRepository;
        this.restaurantTableRepository = restaurantTableRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public Order handleCreateOrder(ReqCreateOrderDTO reqDTO) throws IdInvalidException {
        Order order = new Order();
        order.setStatus(OrderStatusEnum.PENDING);
        order.setPaymentStatus(PaymentStatusEnum.PENDING);
        order.setOrderType(reqDTO.getOrderType());
        order.setNote(reqDTO.getNote());
        if (reqDTO.getOrderType().equals(OrderEnum.IN_STORE)) {
            if (reqDTO.getTableId() == null) {
                throw new IdInvalidException("Đơn hàng ăn tại quán bắt buộc phải chọn bàn (tableId)");
            }

            Optional<RestaurantTable> tableOpt = restaurantTableRepository.findById(reqDTO.getTableId());
            if (tableOpt.isEmpty()) {
                throw new IdInvalidException("Bàn với id = " + reqDTO.getTableId() + " không tồn tại");
            }

            RestaurantTable table = tableOpt.get();
            if (table.getOccupied() == TableEnum.OCCUPIED) {
                throw new IdInvalidException(table.getName() + " hiện đang có khách ngồi!");
            }

            if (table.getOccupied() == TableEnum.RESERVED) {
                throw new IdInvalidException("Bàn " + table.getName() + " đã được đặt trước, vui lòng chọn bàn khác!");
            }

            table.setOccupied(TableEnum.OCCUPIED);

            order.setRestaurantTable(table);

        } else if (reqDTO.getOrderType().equals(OrderEnum.DELIVERY)) {
            order.setRestaurantTable(null);
        }
        long finalTotalPrice = 0;
        List<OrderDetail> listDetails = new ArrayList<>();

        for (ReqCreateOrderDTO.OrderItem item : reqDTO.getItems()) {
            Optional<Dish> dishOpt = dishRepository.findById(item.getDishId());

            if (dishOpt.isEmpty() || !dishOpt.get().isActive()) {
                throw new IdInvalidException(
                        "Món ăn với id = " + item.getDishId() + " không tồn tại hoặc đã ngừng phục vụ");
            }
            Dish dish = dishOpt.get();

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setDish(dish);
            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setNote(item.getNote());
            orderDetail.setHistoricalPrice(dish.getPrice());

            finalTotalPrice += dish.getPrice() * item.getQuantity();

            orderDetail.setOrder(order);
            listDetails.add(orderDetail);
        }

        order.setOrderDetails(listDetails);
        order.setTotalPrice(finalTotalPrice);

        return orderRepository.save(order);
    }

    public ResOrderDTO convertToResOrderDTO(Order order) {
        ResOrderDTO resDTO = new ResOrderDTO();
        resDTO.setId(order.getId());
        resDTO.setTotalPrice(order.getTotalPrice());
        resDTO.setStatus(order.getStatus());
        resDTO.setOrderType(order.getOrderType());
        resDTO.setNote(order.getNote());
        resDTO.setCreatedAt(order.getCreatedAt());
        if (order.getRestaurantTable() != null) {
            resDTO.setTableId(order.getRestaurantTable().getId());
        }
        return resDTO;
    }

    public Order handleFetchOrderById(Long id) throws IdInvalidException {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isEmpty()) {
            throw new IdInvalidException("Đơn hàng với id = " + id + " không tồn tại");
        }
        return orderOpt.get();
    }

    public ResultPaginationDTO fetchAllOrders(Specification<Order> spec, Pageable pageable) {
        Page<Order> page = orderRepository.findAll(spec, pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());
        result.setMeta(meta);
        List<ResOrderDTO> listOrder = page.getContent().stream()
                .map(order -> convertToResOrderDTO(order))
                .toList();
        result.setResult(listOrder);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Order handleUpdateOrder(Long id, ReqCreateOrderDTO reqDTO) throws IdInvalidException {
        Order existingOrder = this.handleFetchOrderById(id);

        if (existingOrder.getStatus() == OrderStatusEnum.PAID
                || existingOrder.getStatus() == OrderStatusEnum.CANCELLED) {
            throw new IdInvalidException("Không thể cập nhật đơn hàng đã thanh toán hoặc đã hủy");
        }

        existingOrder.setNote(reqDTO.getNote());
        existingOrder.setOrderType(reqDTO.getOrderType());
        if (reqDTO.getOrderType() == OrderEnum.IN_STORE && reqDTO.getTableId() != null) {
            Long oldTableId = (existingOrder.getRestaurantTable() != null)
                    ? existingOrder.getRestaurantTable().getId()
                    : null;

            if (!java.util.Objects.equals(reqDTO.getTableId(), oldTableId)) {

                if (existingOrder.getRestaurantTable() != null) {
                    existingOrder.getRestaurantTable().setOccupied(TableEnum.AVAILABLE);
                }

                RestaurantTable newTable = restaurantTableRepository.findById(reqDTO.getTableId())
                        .orElseThrow(() -> new IdInvalidException("Bàn mới không tồn tại"));

                if (newTable.getOccupied() == TableEnum.OCCUPIED) {
                    throw new IdInvalidException("Bàn " + newTable.getName() + " hiện đang có khách!");
                }
                newTable.setOccupied(TableEnum.OCCUPIED);
                existingOrder.setRestaurantTable(newTable);
            }
        }
        existingOrder.getOrderDetails().clear();

        long finalTotalPrice = 0;
        for (ReqCreateOrderDTO.OrderItem item : reqDTO.getItems()) {
            Dish dish = dishRepository.findById(item.getDishId())
                    .orElseThrow(() -> new IdInvalidException("Món ăn không tồn tại"));

            OrderDetail detail = new OrderDetail();
            detail.setDish(dish);
            detail.setQuantity(item.getQuantity());
            detail.setNote(item.getNote());
            detail.setHistoricalPrice(dish.getPrice());
            detail.setOrder(existingOrder);

            existingOrder.getOrderDetails().add(detail);
            finalTotalPrice += dish.getPrice() * item.getQuantity();
        }

        existingOrder.setTotalPrice(finalTotalPrice);

        return orderRepository.save(existingOrder);
    }
}