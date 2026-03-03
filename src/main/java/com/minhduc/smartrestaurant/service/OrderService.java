package com.minhduc.smartrestaurant.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.minhduc.smartrestaurant.domain.Dish;
import com.minhduc.smartrestaurant.domain.Order;
import com.minhduc.smartrestaurant.domain.OrderDetail;
import com.minhduc.smartrestaurant.domain.RestaurantTable;
import com.minhduc.smartrestaurant.domain.request.ReqCreateOrderDTO;
import com.minhduc.smartrestaurant.domain.response.ResOrderDTO;
import com.minhduc.smartrestaurant.repository.DishRepository;
import com.minhduc.smartrestaurant.repository.OrderRepository;
import com.minhduc.smartrestaurant.repository.RestaurantTableRepository;
import com.minhduc.smartrestaurant.util.constant.OrderEnum;
import com.minhduc.smartrestaurant.util.constant.OrderStatusEnum;
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

    public Order handleCreateOrder(ReqCreateOrderDTO reqDTO) throws IdInvalidException {
        Order order = new Order();
        order.setStatus(OrderStatusEnum.PENDING);
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
                throw new IdInvalidException("Bàn " + table.getName() + " hiện đang có khách ngồi!");
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
}