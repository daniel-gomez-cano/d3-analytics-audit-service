package co.empresa.proyecto_desarrollo3.repository;

import co.empresa.proyecto_desarrollo3.model.SalesMetric;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesMetricRepository
        extends JpaRepository<SalesMetric, Long> {
}