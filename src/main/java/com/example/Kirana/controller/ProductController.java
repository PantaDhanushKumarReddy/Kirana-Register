package com.example.Kirana.controller;

import com.example.Kirana.dto.request.ProductRequestDto;
import com.example.Kirana.dto.response.ApiResponse;
import com.example.Kirana.entity.mongo.Product;
import com.example.Kirana.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kirana/products")
public class ProductController {
    private ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @GetMapping("/{productId}")
    public ResponseEntity<Product> getById(@PathVariable String productId) {
        return ResponseEntity.ok(productService.getById(productId));
    }
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(productService.getAll());
    }
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getByCategory(
            @PathVariable String category) {

        return ResponseEntity.ok(
                productService.getByCategory(category)
        );
    }
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @PostMapping
    public ResponseEntity<List<Product>> addProducts(@Valid @RequestBody List<ProductRequestDto> dto){
        return ResponseEntity.ok(productService.addProducts(dto));
    }
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @PostMapping("/{productId}/add-stock")
    public ResponseEntity<ApiResponse> addStock(
            @PathVariable String productId,
            @RequestParam int quantity) {

        productService.addStock(productId, quantity);
        return ResponseEntity.ok(new ApiResponse("Stock added"));
    }
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @PostMapping("/{productId}/reduce-stock")
    public ResponseEntity<ApiResponse> reduceStock(
            @PathVariable String productId,
            @RequestParam int quantity) {

        productService.reduceStock(productId, quantity);
        return ResponseEntity.ok(new ApiResponse("Stock reduced"));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{productId}/deactivate")
    public ResponseEntity<ApiResponse> deactivate(@PathVariable String productId) {
        productService.deactivate(productId);
        return ResponseEntity.ok(
                new ApiResponse("Product deactivated successfully")
        );
    }
}
