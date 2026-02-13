package com.example.Kirana.controller;
import com.example.Kirana.dto.response.FinancialReportResponse;
import com.example.Kirana.service.ReportQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportQueryService service;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<FinancialReportResponse> getReport(
            @RequestParam String periodType,
            @RequestParam String periodKey,
            @RequestParam String currency) {

        return ResponseEntity.ok(
                service.get(periodType, periodKey, currency)
        );
    }
}
