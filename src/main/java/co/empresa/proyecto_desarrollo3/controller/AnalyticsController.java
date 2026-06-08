package co.empresa.proyecto_desarrollo3.controller;

import co.empresa.proyecto_desarrollo3.model.PaymentMetric;
import co.empresa.proyecto_desarrollo3.model.SalesMetric;
import co.empresa.proyecto_desarrollo3.service.AnalyticsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/sales")
    public List<SalesMetric> getSalesMetrics() {
        return analyticsService.getSalesMetrics();
    }

    @PostMapping("/sales")
    public SalesMetric createSalesMetric(
            @RequestBody SalesMetric metric
    ) {
        return analyticsService.saveSalesMetric(metric);
    }

    @GetMapping("/payments")
    public List<PaymentMetric> getPaymentMetrics() {
        return analyticsService.getPaymentMetrics();
    }

    @PostMapping("/payments")
    public PaymentMetric createPaymentMetric(
            @RequestBody PaymentMetric metric
    ) {
        return analyticsService.savePaymentMetric(metric);
    }
}