package com.example.Kirana.controller;

import com.example.Kirana.dto.request.RefundTransactionRequestDto;
import com.example.Kirana.dto.request.SaleTransactionRequestDto;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import com.example.Kirana.dto.response.TransactionResponseDto;
import com.example.Kirana.service.ProductOrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
/**
 * Controller for handling sale and refund transactions.
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final ProductOrderService service;

    public TransactionController(ProductOrderService service) {
        this.service = service;
    }

    private String getKiranaId() {
        Claims claims = (Claims) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getDetails();

        return claims.get("kiranaId", String.class);
    }
    // Process a Sale transaction
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @PostMapping("/sale")
    public ResponseEntity<TransactionResponseDto> sale(
             @RequestBody SaleTransactionRequestDto dto) {
        String kiranaId = getKiranaId();
        return ResponseEntity.ok(service.sale(kiranaId,dto));
    }

    // Process a refund transaction
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @PostMapping("/refund")
    public ResponseEntity<TransactionResponseDto> refund(@RequestBody RefundTransactionRequestDto dto) {
        return ResponseEntity.ok(service.refund(dto));
    }
}
