package com.pr.analisys.demo.service;

import com.pr.analisys.demo.entity.Order;
import com.pr.analisys.demo.entity.OrderItem;
import com.pr.analisys.demo.entity.Product;
import com.pr.analisys.demo.entity.User;
import com.pr.analisys.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Order entity operations
 */
@Service
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ProductService productService;
    
    @Autowired
    public OrderService(OrderRepository orderRepository, UserService userService, ProductService productService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.productService = productService;
    }
    
    /**
     * Create a new order
     * @param order the order to create
     * @return created order
     */
    public Order createOrder(Order order) {
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.PENDING);
        return orderRepository.save(order);
    }
    
    /**
     * Create order with items
     * @param userId user ID
     * @param orderItems list of order items
     * @return created order
     * @throws RuntimeException if user not found or insufficient stock
     */
    public Order createOrderWithItems(Long userId, List<OrderItem> orderItems) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Order order = new Order(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.PENDING);
        
        // Process each order item
        for (OrderItem item : orderItems) {
            Product product = productService.getProductById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + item.getProduct().getId()));
            
            // Check stock availability
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName() + 
                        ". Available: " + product.getStockQuantity() + ", Required: " + item.getQuantity());
            }
            
            // Set current price
            item.setPrice(product.getPrice());
            item.setProduct(product);
            order.addOrderItem(item);
        }
        
        Order savedOrder = orderRepository.save(order);
        
        // Reduce stock for each product
        for (OrderItem item : savedOrder.getOrderItems()) {
            productService.reduceStock(item.getProduct().getId(), item.getQuantity());
        }
        
        return savedOrder;
    }
    
    /**
     * Get all orders
     * @return list of all orders
     */
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    /**
     * Get all orders with items
     * @return list of orders with their items loaded
     */
    @Transactional(readOnly = true)
    public List<Order> getAllOrdersWithItems() {
        return orderRepository.findAllWithOrderItems();
    }
    
    /**
     * Get order by ID
     * @param id order ID
     * @return Optional containing order if found
     */
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
    
    /**
     * Get orders by user ID
     * @param userId user ID
     * @return list of orders for the user
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
    
    /**
     * Get orders by user ID with items
     * @param userId user ID
     * @return list of orders with items for the user
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByUserIdWithItems(Long userId) {
        return orderRepository.findByUserIdWithOrderItems(userId);
    }
    
    /**
     * Get orders by status
     * @param status order status
     * @return list of orders with the specified status
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    /**
     * Get recent orders
     * @return list of orders ordered by date descending
     */
    @Transactional(readOnly = true)
    public List<Order> getRecentOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }
    
    /**
     * Get orders within date range
     * @param startDate start date
     * @param endDate end date
     * @return list of orders within date range
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetween(startDate, endDate);
    }
    
    /**
     * Get orders within amount range
     * @param minAmount minimum amount
     * @param maxAmount maximum amount
     * @return list of orders within amount range
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        return orderRepository.findByTotalAmountBetween(minAmount, maxAmount);
    }
    
    /**
     * Update order status
     * @param id order ID
     * @param status new status
     * @return updated order
     * @throws RuntimeException if order not found
     */
    public Order updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        
        order.setStatus(status);
        return orderRepository.save(order);
    }
    
    /**
     * Cancel an order
     * @param id order ID
     * @return cancelled order
     * @throws RuntimeException if order not found or cannot be cancelled
     */
    public Order cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        
        if (order.getStatus() == Order.OrderStatus.DELIVERED || 
            order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel order with status: " + order.getStatus());
        }
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);
        
        // Restore stock for cancelled order
        for (OrderItem item : cancelledOrder.getOrderItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            product.setIsAvailable(true); // Make available again
            productService.updateProductStock(product.getId(), product.getStockQuantity());
        }
        
        return cancelledOrder;
    }
    
    /**
     * Complete an order (mark as delivered)
     * @param id order ID
     * @return completed order
     * @throws RuntimeException if order not found
     */
    public Order completeOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        
        order.setStatus(Order.OrderStatus.DELIVERED);
        return orderRepository.save(order);
    }
    
    /**
     * Delete an order
     * @param id order ID
     * @throws RuntimeException if order not found
     */
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        
        orderRepository.delete(order);
    }
    
    /**
     * Get count of orders by status
     * @param status order status
     * @return count of orders
     */
    @Transactional(readOnly = true)
    public long getOrderCountByStatus(Order.OrderStatus status) {
        return orderRepository.countByStatus(status);
    }
    
    /**
     * Calculate total revenue
     * @return total revenue from all non-cancelled orders
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue() {
        BigDecimal revenue = orderRepository.calculateTotalRevenue();
        return revenue != null ? revenue : BigDecimal.ZERO;
    }
    
    /**
     * Calculate revenue for date range
     * @param startDate start date
     * @param endDate end date
     * @return revenue within date range
     */
    @Transactional(readOnly = true)
    public BigDecimal getRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal revenue = orderRepository.calculateRevenueByDateRange(startDate, endDate);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }
    
    /**
     * Get today's orders
     * @return list of today's orders
     */
    @Transactional(readOnly = true)
    public List<Order> getTodaysOrders() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        return orderRepository.findByCreatedAtBetween(startOfDay, endOfDay);
    }
}