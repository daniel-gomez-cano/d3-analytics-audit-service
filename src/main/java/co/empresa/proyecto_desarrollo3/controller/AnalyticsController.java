package co.empresa.proyecto_desarrollo3.controller;

import co.empresa.proyecto_desarrollo3.dto.PaymentMetricRequestDTO;
import co.empresa.proyecto_desarrollo3.dto.PaymentMetricResponseDTO;
import co.empresa.proyecto_desarrollo3.dto.SalesMetricRequestDTO;
import co.empresa.proyecto_desarrollo3.dto.SalesMetricResponseDTO;
import co.empresa.proyecto_desarrollo3.service.AnalyticsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/sales")
    public ResponseEntity<List<SalesMetricResponseDTO>> getSalesMetrics() {
        return ResponseEntity.ok(analyticsService.getSalesMetrics());
    }

    @PostMapping("/sales")
    public ResponseEntity<SalesMetricResponseDTO> createSalesMetric(
            @Valid @RequestBody SalesMetricRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(analyticsService.saveSalesMetric(request));
    }

    @GetMapping("/payments")
    public ResponseEntity<List<PaymentMetricResponseDTO>> getPaymentMetrics() {
        return ResponseEntity.ok(analyticsService.getPaymentMetrics());
    }

    @PostMapping("/payments")
    public ResponseEntity<PaymentMetricResponseDTO> createPaymentMetric(
            @Valid @RequestBody PaymentMetricRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(analyticsService.savePaymentMetric(request));
    }
}
