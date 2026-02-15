package com.example.Kirana.controller;

import com.example.Kirana.dto.request.ProductRequestDto;
import com.example.Kirana.dto.response.ApiResponse;
import com.example.Kirana.entity.mongo.Product;
import com.example.Kirana.service.ProductService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * ProductController
 *
 * Exposes APIs for managing products belonging to a Kirana store.
 * Access is restricted based on user roles (ADMIN, STAFF).
 */
@RestController
@RequestMapping("/api/kirana/products")
public class ProductController {
    private ProductService productService;
    /**
     * Constructor-based dependency injection for ProductService.
     *
     * @param productService product service
     */
    public ProductController(ProductService productService) {

        this.productService = productService;
    }
    /**
     * Extracts the Kirana ID from the authenticated user's JWT claims.
     *
     * @return Kirana ID associated with the logged-in user
     */
    private String getKiranaId() {
        Claims claims = (Claims) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getDetails();

        return claims.get("kiranaId", String.class);
    }

    /**
     * Fetch a product by ID.
     *
     * Endpoint: GET /api/kirana/products/{productId}
     * and it's access is restricted to ADMIN and STAFF
     * @param productId Product ID
     * @return Product details
     */
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @GetMapping("/{productId}")
    public ResponseEntity<Product> getById(@PathVariable String productId) {
        return ResponseEntity.ok(productService.getById(productId));
    }
    /**
     * Fetch all active products for the logged-in user's Kirana.
     *
     * Endpoint: GET /api/kirana/products
     *
     * @return List of products
     */
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        String kiranaId= getKiranaId();
        return ResponseEntity.ok(productService.getAll(kiranaId));
    }
    /**
     * Fetch products by category for the logged-in user's Kirana.
     *
     * Endpoint: GET /api/kirana/products/category/{category}
     *
     * @param category Product category
     * @return List of products in the category
     */
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getByCategory(@PathVariable String category) {
        String kiranaId= getKiranaId();
        return ResponseEntity.ok(
                productService.getByCategory(kiranaId,category)
        );
    }
    /**
     * Add new products to the Kirana store.
     *
     * Endpoint: POST /api/kirana/products
     *
     * @param dto List of product creation requests
     * @return List of created products
     */
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @PostMapping
    public ResponseEntity<List<Product>> addProducts(@Valid @RequestBody List<ProductRequestDto> dto){
        String kiranaId= getKiranaId();
        return ResponseEntity.ok(productService.addProducts(kiranaId,dto));
    }
    /**
     * Increase stock quantity for a product.
     *
     * Endpoint: POST /api/kirana/products/{productId}/add-stock
     *
     * @param productId Product ID
     * @param quantity  Quantity to add
     * @return Success response
     */
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @PostMapping("/{productId}/add-stock")
    public ResponseEntity<ApiResponse> addStock(
            @PathVariable String productId,
            @RequestParam int quantity) {

        productService.addStock(productId, quantity);
        return ResponseEntity.ok(new ApiResponse("Stock added"));
    }
    /**
     * Reduce stock quantity for a product.
     *
     * Endpoint: POST /api/kirana/products/{productId}/reduce-stock
     *
     * @param productId Product ID
     * @param quantity  Quantity to reduce
     * @return Success response
     */
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @PostMapping("/{productId}/reduce-stock")
    public ResponseEntity<ApiResponse> reduceStock(
            @PathVariable String productId, @RequestParam int quantity) {

        productService.reduceStock(productId, quantity);
        return ResponseEntity.ok(new ApiResponse("Stock reduced"));
    }
    /**
     * Deactivate a product (soft delete).
     *
     * Endpoint: PATCH /api/kirana/products/{productId}/deactivate
     *
     * Only ADMIN users are allowed to deactivate products.
     *
     * @param productId Product ID
     * @return Success response
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{productId}/deactivate")
    public ResponseEntity<ApiResponse> deactivate(@PathVariable String productId) {
        productService.deactivate(productId);
        return ResponseEntity.ok(
                new ApiResponse("Product deactivated successfully")
        );
    }
}
