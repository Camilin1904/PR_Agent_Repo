package com.pr.analisys.demo.config;

import com.pr.analisys.demo.entity.Order;
import com.pr.analisys.demo.entity.OrderItem;
import com.pr.analisys.demo.entity.Product;
import com.pr.analisys.demo.entity.User;
import com.pr.analisys.demo.repository.OrderRepository;
import com.pr.analisys.demo.repository.ProductRepository;
import com.pr.analisys.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Data initialization component to populate the database with sample data
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    
    @Autowired
    public DataInitializer(UserRepository userRepository, 
                          ProductRepository productRepository, 
                          OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Only initialize data if database is empty
        if (userRepository.count() == 0) {
            initializeUsers();
            initializeProducts();
            initializeOrders();
            
            System.out.println("Sample data has been loaded successfully!");
            System.out.println("Users: " + userRepository.count());
            System.out.println("Products: " + productRepository.count());
            System.out.println("Orders: " + orderRepository.count());
        }
    }
    
    private void initializeUsers() {
        List<User> users = Arrays.asList(
            new User("John", "Doe", "john.doe@email.com", "+1234567890"),
            new User("Jane", "Smith", "jane.smith@email.com", "+1234567891"),
            new User("Bob", "Johnson", "bob.johnson@email.com", "+1234567892"),
            new User("Alice", "Williams", "alice.williams@email.com", "+1234567893"),
            new User("Charlie", "Brown", "charlie.brown@email.com", "+1234567894"),
            new User("Diana", "Davis", "diana.davis@email.com", "+1234567895"),
            new User("Edward", "Miller", "edward.miller@email.com", "+1234567896"),
            new User("Fiona", "Wilson", "fiona.wilson@email.com", "+1234567897")
        );
        
        userRepository.saveAll(users);
    }
    
    private void initializeProducts() {
        List<Product> products = Arrays.asList(
            // Electronics
            new Product("Laptop Pro 15\"", "High-performance laptop with 16GB RAM and 512GB SSD", 
                       new BigDecimal("1299.99"), 25, "Electronics"),
            new Product("Smartphone X1", "Latest smartphone with advanced camera and fast processor", 
                       new BigDecimal("899.99"), 50, "Electronics"),
            new Product("Wireless Headphones", "Premium noise-cancelling wireless headphones", 
                       new BigDecimal("299.99"), 75, "Electronics"),
            new Product("4K Monitor", "27-inch 4K UHD monitor with USB-C connectivity", 
                       new BigDecimal("399.99"), 30, "Electronics"),
            new Product("Gaming Mouse", "High-precision gaming mouse with RGB lighting", 
                       new BigDecimal("79.99"), 100, "Electronics"),
            
            // Books
            new Product("Spring Boot in Action", "Comprehensive guide to Spring Boot development", 
                       new BigDecimal("49.99"), 40, "Books"),
            new Product("Clean Code", "A handbook of agile software craftsmanship", 
                       new BigDecimal("42.99"), 35, "Books"),
            new Product("Design Patterns", "Elements of reusable object-oriented software", 
                       new BigDecimal("54.99"), 28, "Books"),
            new Product("Java: The Complete Reference", "Complete guide to Java programming", 
                       new BigDecimal("59.99"), 32, "Books"),
            
            // Clothing
            new Product("Cotton T-Shirt", "Comfortable 100% cotton t-shirt", 
                       new BigDecimal("19.99"), 150, "Clothing"),
            new Product("Denim Jeans", "Classic fit denim jeans", 
                       new BigDecimal("69.99"), 80, "Clothing"),
            new Product("Running Shoes", "Lightweight running shoes with advanced cushioning", 
                       new BigDecimal("129.99"), 60, "Clothing"),
            new Product("Winter Jacket", "Warm winter jacket with water-resistant coating", 
                       new BigDecimal("199.99"), 35, "Clothing"),
            
            // Home & Garden
            new Product("Coffee Maker", "Programmable coffee maker with thermal carafe", 
                       new BigDecimal("89.99"), 45, "Home & Garden"),
            new Product("Plant Pot Set", "Set of 3 ceramic plant pots with drainage holes", 
                       new BigDecimal("29.99"), 85, "Home & Garden"),
            new Product("LED Desk Lamp", "Adjustable LED desk lamp with USB charging port", 
                       new BigDecimal("39.99"), 70, "Home & Garden"),
            
            // Sports
            new Product("Yoga Mat", "Non-slip yoga mat with carrying strap", 
                       new BigDecimal("24.99"), 90, "Sports"),
            new Product("Dumbbells Set", "Adjustable dumbbells set (10-50 lbs)", 
                       new BigDecimal("199.99"), 20, "Sports"),
            new Product("Tennis Racket", "Professional grade tennis racket", 
                       new BigDecimal("149.99"), 25, "Sports")
        );
        
        productRepository.saveAll(products);
    }
    
    private void initializeOrders() {
        List<User> users = userRepository.findAll();
        List<Product> products = productRepository.findAll();
        
        if (users.isEmpty() || products.isEmpty()) {
            return;
        }
        
        // Create sample orders
        Order order1 = new Order(users.get(0));
        order1.setStatus(Order.OrderStatus.DELIVERED);
        order1.setOrderDate(LocalDateTime.now().minusDays(5));
        
        OrderItem item1 = new OrderItem(order1, products.get(0), 1, products.get(0).getPrice());
        OrderItem item2 = new OrderItem(order1, products.get(2), 1, products.get(2).getPrice());
        
        order1.addOrderItem(item1);
        order1.addOrderItem(item2);
        
        Order order2 = new Order(users.get(1));
        order2.setStatus(Order.OrderStatus.SHIPPED);
        order2.setOrderDate(LocalDateTime.now().minusDays(2));
        
        OrderItem item3 = new OrderItem(order2, products.get(1), 2, products.get(1).getPrice());
        OrderItem item4 = new OrderItem(order2, products.get(9), 3, products.get(9).getPrice());
        
        order2.addOrderItem(item3);
        order2.addOrderItem(item4);
        
        Order order3 = new Order(users.get(2));
        order3.setStatus(Order.OrderStatus.PROCESSING);
        order3.setOrderDate(LocalDateTime.now().minusDays(1));
        
        OrderItem item5 = new OrderItem(order3, products.get(5), 1, products.get(5).getPrice());
        OrderItem item6 = new OrderItem(order3, products.get(6), 1, products.get(6).getPrice());
        OrderItem item7 = new OrderItem(order3, products.get(7), 1, products.get(7).getPrice());
        
        order3.addOrderItem(item5);
        order3.addOrderItem(item6);
        order3.addOrderItem(item7);
        
        Order order4 = new Order(users.get(3));
        order4.setStatus(Order.OrderStatus.PENDING);
        order4.setOrderDate(LocalDateTime.now());
        
        OrderItem item8 = new OrderItem(order4, products.get(11), 1, products.get(11).getPrice());
        OrderItem item9 = new OrderItem(order4, products.get(16), 1, products.get(16).getPrice());
        
        order4.addOrderItem(item8);
        order4.addOrderItem(item9);
        
        Order order5 = new Order(users.get(4));
        order5.setStatus(Order.OrderStatus.CONFIRMED);
        order5.setOrderDate(LocalDateTime.now().minusHours(6));
        
        OrderItem item10 = new OrderItem(order5, products.get(13), 1, products.get(13).getPrice());
        
        order5.addOrderItem(item10);
        
        // Save orders
        orderRepository.saveAll(Arrays.asList(order1, order2, order3, order4, order5));
        
        // Update product stock quantities to reflect the orders
        products.get(0).setStockQuantity(products.get(0).getStockQuantity() - 1);
        products.get(1).setStockQuantity(products.get(1).getStockQuantity() - 2);
        products.get(2).setStockQuantity(products.get(2).getStockQuantity() - 1);
        products.get(5).setStockQuantity(products.get(5).getStockQuantity() - 1);
        products.get(6).setStockQuantity(products.get(6).getStockQuantity() - 1);
        products.get(7).setStockQuantity(products.get(7).getStockQuantity() - 1);
        products.get(9).setStockQuantity(products.get(9).getStockQuantity() - 3);
        products.get(11).setStockQuantity(products.get(11).getStockQuantity() - 1);
        products.get(13).setStockQuantity(products.get(13).getStockQuantity() - 1);
        products.get(16).setStockQuantity(products.get(16).getStockQuantity() - 1);
        
        productRepository.saveAll(products);
    }
}