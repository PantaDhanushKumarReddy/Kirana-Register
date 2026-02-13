package com.example.Kirana.controller;

import com.example.Kirana.dto.request.RefundTransactionRequestDto;
import com.example.Kirana.dto.request.SaleTransactionRequestDto;
import org.springframework.web.bind.annotation.RestController;

import com.example.Kirana.dto.response.TransactionResponseDto;
import com.example.Kirana.service.ProductOrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final ProductOrderService service;
    public TransactionController(ProductOrderService service) {
        this.service = service;
    }
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @PostMapping("/sale")
    public ResponseEntity<TransactionResponseDto> sale(
             @RequestBody SaleTransactionRequestDto dto) {
        return ResponseEntity.ok(service.sale(dto));
    }
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @PostMapping("/refund")
    public ResponseEntity<TransactionResponseDto> refund(@RequestBody RefundTransactionRequestDto dto) {
        return ResponseEntity.ok(service.refund(dto));
    }
}
