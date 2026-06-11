package co.empresa.proyecto_desarrollo3.repository;

import co.empresa.proyecto_desarrollo3.model.SalesMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalesMetricRepository extends JpaRepository<SalesMetric, Long> {

    Optional<SalesMetric> findByEventId(Long eventId);

    List<SalesMetric> findAllByOrderByUpdatedAtDesc();

    @Query("SELECT SUM(s.revenue) FROM SalesMetric s")
    Double sumTotalRevenue();

    @Query("SELECT SUM(s.ticketsSold) FROM SalesMetric s")
    Integer sumTotalTicketsSold();
}
