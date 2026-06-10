package co.empresa.proyecto_desarrollo3.service;

import co.empresa.proyecto_desarrollo3.dto.PaymentMetricRequestDTO;
import co.empresa.proyecto_desarrollo3.dto.PaymentMetricResponseDTO;
import co.empresa.proyecto_desarrollo3.dto.SalesMetricRequestDTO;
import co.empresa.proyecto_desarrollo3.dto.SalesMetricResponseDTO;
import co.empresa.proyecto_desarrollo3.model.PaymentMetric;
import co.empresa.proyecto_desarrollo3.model.SalesMetric;
import co.empresa.proyecto_desarrollo3.repository.PaymentMetricRepository;
import co.empresa.proyecto_desarrollo3.repository.SalesMetricRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final SalesMetricRepository salesMetricRepository;
    private final PaymentMetricRepository paymentMetricRepository;

    // ── Sales ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<SalesMetricResponseDTO> getSalesMetrics() {
        return salesMetricRepository.findAllByOrderByUpdatedAtDesc()
                .stream()
                .map(this::toSalesResponse)
                .toList();
    }

    /**
     * Upsert: si ya existe una métrica para ese eventId la acumula,
     * si no existe la crea. Así cada venta suma a la métrica del evento.
     */
    @Transactional
    public SalesMetricResponseDTO saveSalesMetric(SalesMetricRequestDTO request) {
        SalesMetric metric = salesMetricRepository
                .findByEventId(request.getEventId())
                .orElse(SalesMetric.builder()
                        .eventId(request.getEventId())
                        .ticketsSold(0)
                        .revenue(0.0)
                        .build());

        metric.setTicketsSold(
                (metric.getTicketsSold() == null ? 0 : metric.getTicketsSold())
                + (request.getTicketsSold() == null ? 0 : request.getTicketsSold())
        );
        metric.setRevenue(
                (metric.getRevenue() == null ? 0.0 : metric.getRevenue())
                + (request.getRevenue() == null ? 0.0 : request.getRevenue())
        );

        SalesMetric saved = salesMetricRepository.save(metric);
        log.info("SalesMetric upserted: eventId={}, totalTickets={}, totalRevenue={}",
                saved.getEventId(), saved.getTicketsSold(), saved.getRevenue());
        return toSalesResponse(saved);
    }

    // ── Payments ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<PaymentMetricResponseDTO> getPaymentMetrics() {
        return paymentMetricRepository.findAll()
                .stream()
                .map(this::toPaymentResponse)
                .toList();
    }

    @Transactional
    public PaymentMetricResponseDTO savePaymentMetric(PaymentMetricRequestDTO request) {
        PaymentMetric metric = PaymentMetric.builder()
                .approvedCount(request.getApprovedCount() == null ? 0 : request.getApprovedCount())
                .rejectedCount(request.getRejectedCount() == null ? 0 : request.getRejectedCount())
                .pendingCount(request.getPendingCount() == null ? 0 : request.getPendingCount())
                .build();

        PaymentMetric saved = paymentMetricRepository.save(metric);
        log.info("PaymentMetric saved: approved={}, rejected={}, pending={}",
                saved.getApprovedCount(), saved.getRejectedCount(), saved.getPendingCount());
        return toPaymentResponse(saved);
    }

    /**
     * Llamado desde el RabbitMQ listener cuando llega confirmación de pago.
     */
    @Transactional
    public void recordPaymentEvent(String paymentStatus, Long eventId,
                                   Integer ticketCount, Double amount) {
        // Registrar en sales metrics
        SalesMetricRequestDTO salesDTO = SalesMetricRequestDTO.builder()
                .eventId(eventId)
                .ticketsSold("APPROVED".equalsIgnoreCase(paymentStatus) ? ticketCount : 0)
                .revenue("APPROVED".equalsIgnoreCase(paymentStatus) ? amount : 0.0)
                .build();
        saveSalesMetric(salesDTO);

        // Registrar en payment metrics
        PaymentMetric metric = PaymentMetric.builder()
                .approvedCount("APPROVED".equalsIgnoreCase(paymentStatus) ? 1 : 0)
                .rejectedCount("REJECTED".equalsIgnoreCase(paymentStatus) ? 1 : 0)
                .pendingCount("PENDING".equalsIgnoreCase(paymentStatus) ? 1 : 0)
                .build();
        paymentMetricRepository.save(metric);

        log.info("Payment event recorded: status={}, eventId={}, tickets={}, amount={}",
                paymentStatus, eventId, ticketCount, amount);
    }

    // ── mappers ───────────────────────────────────────────────────────────────

    private SalesMetricResponseDTO toSalesResponse(SalesMetric m) {
        return SalesMetricResponseDTO.builder()
                .id(m.getId())
                .eventId(m.getEventId())
                .ticketsSold(m.getTicketsSold())
                .revenue(m.getRevenue())
                .updatedAt(m.getUpdatedAt())
                .build();
    }

    private PaymentMetricResponseDTO toPaymentResponse(PaymentMetric m) {
        return PaymentMetricResponseDTO.builder()
                .id(m.getId())
                .approvedCount(m.getApprovedCount())
                .rejectedCount(m.getRejectedCount())
                .pendingCount(m.getPendingCount())
                .updatedAt(m.getUpdatedAt())
                .build();
    }
}
