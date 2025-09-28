package com.pr.analisys.demo.repository;

import com.pr.analisys.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product entity operations
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Find all available products
     * @return list of available products
     */
    List<Product> findByIsAvailableTrue();
    
    /**
     * Find products by category
     * @param category the category to search for
     * @return list of products in the specified category
     */
    List<Product> findByCategory(String category);
    
    /**
     * Find products by category and availability
     * @param category the category to search for
     * @param isAvailable availability status
     * @return list of products matching criteria
     */
    List<Product> findByCategoryAndIsAvailable(String category, Boolean isAvailable);
    
    /**
     * Find products by name containing text (case insensitive)
     * @param name part of product name to search for
     * @return list of products with matching name
     */
    List<Product> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find products within price range
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @return list of products within price range
     */
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Find products with stock quantity greater than specified amount
     * @param quantity minimum stock quantity
     * @return list of products with sufficient stock
     */
    List<Product> findByStockQuantityGreaterThan(Integer quantity);
    
    /**
     * Find products with low stock (less than specified quantity)
     * @param quantity threshold quantity
     * @return list of products with low stock
     */
    List<Product> findByStockQuantityLessThan(Integer quantity);
    
    /**
     * Find all distinct categories
     * @return list of product categories
     */
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL ORDER BY p.category")
    List<String> findDistinctCategories();
    
    /**
     * Find top selling products by order count
     * @param limit maximum number of results
     * @return list of top selling products
     */
    @Query("SELECT p FROM Product p LEFT JOIN p.orderItems oi GROUP BY p ORDER BY COUNT(oi) DESC")
    List<Product> findTopSellingProducts(@Param("limit") int limit);
    
    /**
     * Find products by category with available stock
     * @param category product category
     * @return list of available products in category
     */
    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.isAvailable = true AND p.stockQuantity > 0")
    List<Product> findAvailableProductsByCategory(@Param("category") String category);
    
    /**
     * Count products by availability
     * @param isAvailable availability status
     * @return count of products
     */
    long countByIsAvailable(Boolean isAvailable);
    
    /**
     * Find products ordered by price ascending
     * @return list of products ordered by price
     */
    List<Product> findAllByOrderByPriceAsc();
    
    /**
     * Find products ordered by name
     * @return list of products ordered by name
     */
    List<Product> findAllByOrderByNameAsc();
}