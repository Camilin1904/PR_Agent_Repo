package com.pr.analisys.demo.controller;

import com.pr.analisys.demo.entity.Product;
import com.pr.analisys.demo.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Product operations
 */
@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Management", description = "Operations related to product management")
public class ProductController {
    
    private final ProductService productService;
    
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @Operation(summary = "Get all products", description = "Retrieve a list of all products")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of products")
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    @Operation(summary = "Get all available products", description = "Retrieve a list of all available products")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of available products")
    @GetMapping("/available")
    public ResponseEntity<List<Product>> getAvailableProducts() {
        List<Product> products = productService.getAvailableProducts();
        return ResponseEntity.ok(products);
    }
    
    @Operation(summary = "Get product by ID", description = "Retrieve a specific product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get products by category", description = "Retrieve products by category")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved products by category")
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(
            @Parameter(description = "Product category", required = true)
            @PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }
    
    @Operation(summary = "Get available products by category", description = "Retrieve available products in a specific category")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved available products by category")
    @GetMapping("/category/{category}/available")
    public ResponseEntity<List<Product>> getAvailableProductsByCategory(
            @Parameter(description = "Product category", required = true)
            @PathVariable String category) {
        List<Product> products = productService.getAvailableProductsByCategory(category);
        return ResponseEntity.ok(products);
    }
    
    @Operation(summary = "Search products by name", description = "Search for products by name (case insensitive)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved matching products")
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProductsByName(
            @Parameter(description = "Product name to search for", required = true)
            @RequestParam String name) {
        List<Product> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }
    
    @Operation(summary = "Get products by price range", description = "Retrieve products within a specific price range")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved products by price range")
    @GetMapping("/price-range")
    public ResponseEntity<List<Product>> getProductsByPriceRange(
            @Parameter(description = "Minimum price", required = true)
            @RequestParam BigDecimal minPrice,
            @Parameter(description = "Maximum price", required = true)
            @RequestParam BigDecimal maxPrice) {
        List<Product> products = productService.getProductsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }
    
    @Operation(summary = "Get low stock products", description = "Retrieve products with stock below specified threshold")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved low stock products")
    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts(
            @Parameter(description = "Stock threshold", required = true)
            @RequestParam Integer threshold) {
        List<Product> products = productService.getLowStockProducts(threshold);
        return ResponseEntity.ok(products);
    }
    
    @Operation(summary = "Get all product categories", description = "Retrieve list of all distinct product categories")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved product categories")
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = productService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    @Operation(summary = "Get products ordered by price", description = "Retrieve products ordered by price ascending")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved products ordered by price")
    @GetMapping("/ordered-by-price")
    public ResponseEntity<List<Product>> getProductsByPriceAscending() {
        List<Product> products = productService.getProductsByPriceAscending();
        return ResponseEntity.ok(products);
    }
    
    @Operation(summary = "Create a new product", description = "Create a new product in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<Product> createProduct(
            @Parameter(description = "Product data", required = true)
            @Valid @RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }
    
    @Operation(summary = "Update a product", description = "Update an existing product's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated product data", required = true)
            @Valid @RequestBody Product productDetails) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDetails);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Update product stock", description = "Update the stock quantity of a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Product> updateProductStock(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "New stock quantity", required = true)
            @RequestParam Integer stock) {
        try {
            Product updatedProduct = productService.updateProductStock(id, stock);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Toggle product availability", description = "Toggle the availability status of a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Availability toggled successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PatchMapping("/{id}/toggle-availability")
    public ResponseEntity<Product> toggleAvailability(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id) {
        try {
            Product updatedProduct = productService.toggleAvailability(id);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Delete a product", description = "Permanently delete a product from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Get available product count", description = "Get the total count of available products")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved available product count")
    @GetMapping("/count/available")
    public ResponseEntity<Long> getAvailableProductCount() {
        long count = productService.getAvailableProductCount();
        return ResponseEntity.ok(count);
    }
}