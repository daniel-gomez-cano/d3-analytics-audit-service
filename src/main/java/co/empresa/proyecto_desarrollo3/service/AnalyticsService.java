package co.empresa.proyecto_desarrollo3.service;

import co.empresa.proyecto_desarrollo3.model.PaymentMetric;
import co.empresa.proyecto_desarrollo3.model.SalesMetric;
import co.empresa.proyecto_desarrollo3.repository.PaymentMetricRepository;
import co.empresa.proyecto_desarrollo3.repository.SalesMetricRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsService {

    private final SalesMetricRepository salesMetricRepository;
    private final PaymentMetricRepository paymentMetricRepository;

    public AnalyticsService(
            SalesMetricRepository salesMetricRepository,
            PaymentMetricRepository paymentMetricRepository
    ) {
        this.salesMetricRepository = salesMetricRepository;
        this.paymentMetricRepository = paymentMetricRepository;
    }

    public List<SalesMetric> getSalesMetrics() {
        return salesMetricRepository.findAll();
    }

    public List<PaymentMetric> getPaymentMetrics() {
        return paymentMetricRepository.findAll();
    }

    public SalesMetric saveSalesMetric(
            SalesMetric metric
    ) {
        return salesMetricRepository.save(metric);
    }

    public PaymentMetric savePaymentMetric(
            PaymentMetric metric
    ) {
        return paymentMetricRepository.save(metric);
    }
}