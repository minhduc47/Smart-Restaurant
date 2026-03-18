package com.minhduc.smartrestaurant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.minhduc.smartrestaurant.domain.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    @Query(value = """
            SELECT COALESCE(SUM(o.total_price), 0)
            FROM orders o
            WHERE o.status = 'PAID'
            """, nativeQuery = true)
    Long getTotalRevenueFromPaidOrders();

    @Query(value = """
            SELECT o.status AS status, COUNT(*) AS total
            FROM orders o
            GROUP BY o.status
            """, nativeQuery = true)
    List<OrderStatusCountProjection> countOrdersByStatus();

    @Query(value = """
            SELECT COALESCE(o.payment_method, 'UNKNOWN') AS paymentMethod,
            	   COALESCE(SUM(o.total_price), 0) AS revenue
            FROM orders o
            WHERE o.status = 'PAID'
            GROUP BY o.payment_method
            """, nativeQuery = true)
    List<RevenueByMethodProjection> getRevenueByPaymentMethod();

    interface OrderStatusCountProjection {
        String getStatus();

        Long getTotal();
    }

    interface RevenueByMethodProjection {
        String getPaymentMethod();

        Long getRevenue();
    }

}
