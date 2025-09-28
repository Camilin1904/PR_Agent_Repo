package com.pr.analisys.demo.repository;

import com.pr.analisys.demo.entity.Order;
import com.pr.analisys.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order entity operations
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Find orders by user
     * @param user the user to search orders for
     * @return list of orders for the user
     */
    List<Order> findByUser(User user);
    
    /**
     * Find orders by user ID
     * @param userId the user ID to search orders for
     * @return list of orders for the user
     */
    List<Order> findByUserId(Long userId);
    
    /**
     * Find orders by status
     * @param status the order status
     * @return list of orders with the specified status
     */
    List<Order> findByStatus(Order.OrderStatus status);
    
    /**
     * Find orders by user and status
     * @param user the user
     * @param status the order status
     * @return list of orders matching criteria
     */
    List<Order> findByUserAndStatus(User user, Order.OrderStatus status);
    
    /**
     * Find orders within date range
     * @param startDate start date
     * @param endDate end date
     * @return list of orders within date range
     */
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find orders with total amount greater than specified value
     * @param amount minimum total amount
     * @return list of orders with total amount greater than specified
     */
    List<Order> findByTotalAmountGreaterThan(BigDecimal amount);
    
    /**
     * Find orders with total amount between range
     * @param minAmount minimum amount
     * @param maxAmount maximum amount
     * @return list of orders within amount range
     */
    List<Order> findByTotalAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    /**
     * Find recent orders (ordered by date descending)
     * @return list of recent orders
     */
    List<Order> findAllByOrderByOrderDateDesc();
    
    /**
     * Find orders by user ordered by date descending
     * @param user the user
     * @return list of user's orders ordered by date
     */
    List<Order> findByUserOrderByOrderDateDesc(User user);
    
    /**
     * Find orders with order items
     * @return list of orders with their items
     */
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product")
    List<Order> findAllWithOrderItems();
    
    /**
     * Find orders by user with order items
     * @param userId user ID
     * @return list of orders with items for the user
     */
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product WHERE o.user.id = :userId")
    List<Order> findByUserIdWithOrderItems(@Param("userId") Long userId);
    
    /**
     * Count orders by status
     * @param status order status
     * @return count of orders with the status
     */
    long countByStatus(Order.OrderStatus status);
    
    /**
     * Count orders by user
     * @param user the user
     * @return count of orders for the user
     */
    long countByUser(User user);
    
    /**
     * Find top customers by order count
     * @return list of user IDs with order counts
     */
    @Query("SELECT o.user.id, COUNT(o) FROM Order o GROUP BY o.user.id ORDER BY COUNT(o) DESC")
    List<Object[]> findTopCustomersByOrderCount();
    
    /**
     * Calculate total revenue
     * @return total revenue from all orders
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status != 'CANCELLED'")
    BigDecimal calculateTotalRevenue();
    
    /**
     * Calculate revenue by date range
     * @param startDate start date
     * @param endDate end date
     * @return revenue within date range
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate AND o.status != 'CANCELLED'")
    BigDecimal calculateRevenueByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find orders created today
     * @param startOfDay start of current day
     * @param endOfDay end of current day
     * @return list of today's orders
     */
    List<Order> findByCreatedAtBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
}