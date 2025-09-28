package com.pr.analisys.demo.service;

import com.pr.analisys.demo.entity.Product;
import com.pr.analisys.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Product entity operations
 */
@Service
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    /**
     * Create a new product
     * @param product the product to create
     * @return created product
     */
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    
    /**
     * Get all products
     * @return list of all products
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    /**
     * Get all available products
     * @return list of available products
     */
    @Transactional(readOnly = true)
    public List<Product> getAvailableProducts() {
        return productRepository.findByIsAvailableTrue();
    }
    
    /**
     * Get product by ID
     * @param id product ID
     * @return Optional containing product if found
     */
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    /**
     * Get products by category
     * @param category product category
     * @return list of products in category
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    /**
     * Get available products by category
     * @param category product category
     * @return list of available products in category
     */
    @Transactional(readOnly = true)
    public List<Product> getAvailableProductsByCategory(String category) {
        return productRepository.findAvailableProductsByCategory(category);
    }
    
    /**
     * Search products by name
     * @param name product name to search
     * @return list of products with matching name
     */
    @Transactional(readOnly = true)
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * Get products within price range
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @return list of products within price range
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }
    
    /**
     * Get products with low stock
     * @param threshold stock threshold
     * @return list of products with low stock
     */
    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts(Integer threshold) {
        return productRepository.findByStockQuantityLessThan(threshold);
    }
    
    /**
     * Get products with sufficient stock
     * @param minQuantity minimum stock quantity
     * @return list of products with sufficient stock
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsWithSufficientStock(Integer minQuantity) {
        return productRepository.findByStockQuantityGreaterThan(minQuantity);
    }
    
    /**
     * Get all product categories
     * @return list of distinct categories
     */
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return productRepository.findDistinctCategories();
    }
    
    /**
     * Get products ordered by price
     * @return list of products ordered by price ascending
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsByPriceAscending() {
        return productRepository.findAllByOrderByPriceAsc();
    }
    
    /**
     * Get products ordered by name
     * @return list of products ordered by name
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsByNameAscending() {
        return productRepository.findAllByOrderByNameAsc();
    }
    
    /**
     * Update an existing product
     * @param id product ID
     * @param productDetails updated product details
     * @return updated product
     * @throws RuntimeException if product not found
     */
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStockQuantity(productDetails.getStockQuantity());
        product.setIsAvailable(productDetails.getIsAvailable());
        product.setCategory(productDetails.getCategory());
        
        return productRepository.save(product);
    }
    
    /**
     * Update product stock
     * @param id product ID
     * @param newStock new stock quantity
     * @return updated product
     * @throws RuntimeException if product not found
     */
    public Product updateProductStock(Long id, Integer newStock) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        product.setStockQuantity(newStock);
        // Automatically set availability based on stock
        product.setIsAvailable(newStock > 0);
        
        return productRepository.save(product);
    }
    
    /**
     * Reduce product stock (for order processing)
     * @param id product ID
     * @param quantity quantity to reduce
     * @return updated product
     * @throws RuntimeException if product not found or insufficient stock
     */
    public Product reduceStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + 
                    product.getStockQuantity() + ", Required: " + quantity);
        }
        
        product.setStockQuantity(product.getStockQuantity() - quantity);
        // Set unavailable if stock reaches zero
        if (product.getStockQuantity() == 0) {
            product.setIsAvailable(false);
        }
        
        return productRepository.save(product);
    }
    
    /**
     * Toggle product availability
     * @param id product ID
     * @return updated product
     * @throws RuntimeException if product not found
     */
    public Product toggleAvailability(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        product.setIsAvailable(!product.getIsAvailable());
        return productRepository.save(product);
    }
    
    /**
     * Delete a product
     * @param id product ID
     * @throws RuntimeException if product not found
     */
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        productRepository.delete(product);
    }
    
    /**
     * Get count of available products
     * @return number of available products
     */
    @Transactional(readOnly = true)
    public long getAvailableProductCount() {
        return productRepository.countByIsAvailable(true);
    }
    
    /**
     * Get count of products by category
     * @param category product category
     * @return number of products in category
     */
    @Transactional(readOnly = true)
    public long getProductCountByCategory(String category) {
        return productRepository.findByCategory(category).size();
    }
}