package com.minhduc.smartrestaurant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.minhduc.smartrestaurant.domain.OrderDetail;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    @Query(value = """
            SELECT od.dish_id AS dishId,
                   d.name AS dishName,
                   COALESCE(SUM(od.quantity), 0) AS totalQuantity
            FROM order_details od
            JOIN dishes d ON d.id = od.dish_id
            GROUP BY od.dish_id, d.name
            ORDER BY totalQuantity DESC
            LIMIT 5
            """, nativeQuery = true)
    List<TopSellingDishProjection> findTopSellingDishes();

    interface TopSellingDishProjection {
        Long getDishId();

        String getDishName();

        Long getTotalQuantity();
    }
}