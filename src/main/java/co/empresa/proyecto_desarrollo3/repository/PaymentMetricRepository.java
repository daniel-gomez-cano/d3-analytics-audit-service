package co.empresa.proyecto_desarrollo3.repository;

import co.empresa.proyecto_desarrollo3.model.PaymentMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentMetricRepository extends JpaRepository<PaymentMetric, Long> {

    @Query("SELECT SUM(p.approvedCount) FROM PaymentMetric p")
    Integer sumApproved();

    @Query("SELECT SUM(p.rejectedCount) FROM PaymentMetric p")
    Integer sumRejected();

    @Query("SELECT SUM(p.pendingCount) FROM PaymentMetric p")
    Integer sumPending();
}
