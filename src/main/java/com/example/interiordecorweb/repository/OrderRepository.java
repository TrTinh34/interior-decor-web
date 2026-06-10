package com.example.interiordecorweb.repository;

import com.example.interiordecorweb.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUserIdOrderByOrderDateDesc(Integer userId);

    // Doanh thu theo ngày
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
            "WHERE o.status = 'PAID' AND CAST(o.orderDate AS LocalDate) = :date")
    BigDecimal sumRevenueByDate(@Param("date") LocalDate date);

    // Doanh thu theo tháng
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
            "WHERE o.status = 'PAID' " +
            "AND YEAR(o.orderDate) = :year AND MONTH(o.orderDate) = :month")
    BigDecimal sumRevenueByMonth(@Param("year") int year, @Param("month") int month);

    // Doanh thu theo năm
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
            "WHERE o.status = 'PAID' AND YEAR(o.orderDate) = :year")
    BigDecimal sumRevenueByYear(@Param("year") int year);

    // Doanh thu 7 ngày (cho biểu đồ)
    @Query("SELECT CAST(o.orderDate AS LocalDate) as date, " +
            "COALESCE(SUM(o.totalAmount), 0) as revenue " +
            "FROM Order o WHERE o.status = 'PAID' " +
            "AND o.orderDate >= :startDate AND o.orderDate <= :endDate " +
            "GROUP BY CAST(o.orderDate AS LocalDate) " +
            "ORDER BY CAST(o.orderDate AS LocalDate)")
    List<Object[]> revenueByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
