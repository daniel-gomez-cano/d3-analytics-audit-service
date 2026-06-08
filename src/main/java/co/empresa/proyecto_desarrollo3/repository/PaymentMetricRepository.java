package co.empresa.proyecto_desarrollo3.repository;

import co.empresa.proyecto_desarrollo3.model.PaymentMetric;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMetricRepository
        extends JpaRepository<PaymentMetric, Long> {
}