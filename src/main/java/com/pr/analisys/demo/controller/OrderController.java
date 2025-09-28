package com.pr.analisys.demo.controller;

import com.pr.analisys.demo.entity.Order;
import com.pr.analisys.demo.entity.OrderItem;
import com.pr.analisys.demo.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Order operations
 */
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "Operations related to order management")
public class OrderController {
    
    private final OrderService orderService;
    
    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @Operation(summary = "Get all orders", description = "Retrieve a list of all orders")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of orders")
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
    
    @Operation(summary = "Get all orders with items", description = "Retrieve a list of all orders with their order items")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of orders with items")
    @GetMapping("/with-items")
    public ResponseEntity<List<Order>> getAllOrdersWithItems() {
        List<Order> orders = orderService.getAllOrdersWithItems();
        return ResponseEntity.ok(orders);
    }
    
    @Operation(summary = "Get order by ID", description = "Retrieve a specific order by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long id) {
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get orders by user ID", description = "Retrieve orders for a specific user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user orders")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }
    
    @Operation(summary = "Get orders by user ID with items", description = "Retrieve orders for a user with their order items")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user orders with items")
    @GetMapping("/user/{userId}/with-items")
    public ResponseEntity<List<Order>> getOrdersByUserIdWithItems(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId) {
        List<Order> orders = orderService.getOrdersByUserIdWithItems(userId);
        return ResponseEntity.ok(orders);
    }
    
    @Operation(summary = "Get orders by status", description = "Retrieve orders with a specific status")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved orders by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(
            @Parameter(description = "Order status", required = true)
            @PathVariable Order.OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }
    
    @Operation(summary = "Get recent orders", description = "Retrieve orders ordered by date descending")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved recent orders")
    @GetMapping("/recent")
    public ResponseEntity<List<Order>> getRecentOrders() {
        List<Order> orders = orderService.getRecentOrders();
        return ResponseEntity.ok(orders);
    }
    
    @Operation(summary = "Get orders by date range", description = "Retrieve orders within a specific date range")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved orders by date range")
    @GetMapping("/date-range")
    public ResponseEntity<List<Order>> getOrdersByDateRange(
            @Parameter(description = "Start date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Order> orders = orderService.getOrdersByDateRange(startDate, endDate);
        return ResponseEntity.ok(orders);
    }
    
    @Operation(summary = "Get orders by amount range", description = "Retrieve orders within a specific amount range")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved orders by amount range")
    @GetMapping("/amount-range")
    public ResponseEntity<List<Order>> getOrdersByAmountRange(
            @Parameter(description = "Minimum amount", required = true)
            @RequestParam BigDecimal minAmount,
            @Parameter(description = "Maximum amount", required = true)
            @RequestParam BigDecimal maxAmount) {
        List<Order> orders = orderService.getOrdersByAmountRange(minAmount, maxAmount);
        return ResponseEntity.ok(orders);
    }
    
    @Operation(summary = "Get today's orders", description = "Retrieve all orders created today")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved today's orders")
    @GetMapping("/today")
    public ResponseEntity<List<Order>> getTodaysOrders() {
        List<Order> orders = orderService.getTodaysOrders();
        return ResponseEntity.ok(orders);
    }
    
    @Operation(summary = "Create a new order", description = "Create a new order in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<Order> createOrder(
            @Parameter(description = "Order data", required = true)
            @Valid @RequestBody Order order) {
        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }
    
    @Operation(summary = "Create order with items", description = "Create a new order with order items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or insufficient stock"),
            @ApiResponse(responseCode = "404", description = "User or product not found")
    })
    @PostMapping("/user/{userId}/with-items")
    public ResponseEntity<Order> createOrderWithItems(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "List of order items", required = true)
            @Valid @RequestBody List<OrderItem> orderItems) {
        try {
            Order createdOrder = orderService.createOrderWithItems(userId, orderItems);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else if (e instanceof IllegalArgumentException) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Operation(summary = "Update order status", description = "Update the status of an existing order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "New order status", required = true)
            @RequestParam Order.OrderStatus status) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Cancel an order", description = "Cancel an order and restore stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Order cannot be cancelled"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long id) {
        try {
            Order cancelledOrder = orderService.cancelOrder(id);
            return ResponseEntity.ok(cancelledOrder);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else if (e instanceof IllegalStateException) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Operation(summary = "Complete an order", description = "Mark an order as delivered")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order completed successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Order> completeOrder(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long id) {
        try {
            Order completedOrder = orderService.completeOrder(id);
            return ResponseEntity.ok(completedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Delete an order", description = "Permanently delete an order from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Get order count by status", description = "Get the count of orders for a specific status")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved order count")
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> getOrderCountByStatus(
            @Parameter(description = "Order status", required = true)
            @PathVariable Order.OrderStatus status) {
        long count = orderService.getOrderCountByStatus(status);
        return ResponseEntity.ok(count);
    }
    
    @Operation(summary = "Get total revenue", description = "Get the total revenue from all non-cancelled orders")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved total revenue")
    @GetMapping("/revenue/total")
    public ResponseEntity<BigDecimal> getTotalRevenue() {
        BigDecimal revenue = orderService.getTotalRevenue();
        return ResponseEntity.ok(revenue);
    }
    
    @Operation(summary = "Get revenue by date range", description = "Get revenue within a specific date range")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved revenue by date range")
    @GetMapping("/revenue/date-range")
    public ResponseEntity<BigDecimal> getRevenueByDateRange(
            @Parameter(description = "Start date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        BigDecimal revenue = orderService.getRevenueByDateRange(startDate, endDate);
        return ResponseEntity.ok(revenue);
    }
}