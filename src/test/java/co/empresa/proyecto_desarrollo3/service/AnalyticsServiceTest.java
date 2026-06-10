package co.empresa.proyecto_desarrollo3.service;

import co.empresa.proyecto_desarrollo3.dto.PaymentMetricRequestDTO;
import co.empresa.proyecto_desarrollo3.dto.PaymentMetricResponseDTO;
import co.empresa.proyecto_desarrollo3.dto.SalesMetricRequestDTO;
import co.empresa.proyecto_desarrollo3.dto.SalesMetricResponseDTO;
import co.empresa.proyecto_desarrollo3.model.PaymentMetric;
import co.empresa.proyecto_desarrollo3.model.SalesMetric;
import co.empresa.proyecto_desarrollo3.repository.PaymentMetricRepository;
import co.empresa.proyecto_desarrollo3.repository.SalesMetricRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private SalesMetricRepository salesMetricRepository;

    @Mock
    private PaymentMetricRepository paymentMetricRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    private SalesMetric sampleSales;
    private PaymentMetric samplePayment;

    @BeforeEach
    void setUp() {
        sampleSales = SalesMetric.builder()
                .id(1L)
                .eventId(10L)
                .ticketsSold(50)
                .revenue(500000.0)
                .updatedAt(LocalDateTime.now())
                .build();

        samplePayment = PaymentMetric.builder()
                .id(1L)
                .approvedCount(40)
                .rejectedCount(5)
                .pendingCount(5)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ── Sales ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getSalesMetrics - retorna lista mapeada a DTOs")
    void getSalesMetrics_shouldReturnMappedList() {
        when(salesMetricRepository.findAllByOrderByUpdatedAtDesc())
                .thenReturn(List.of(sampleSales));

        List<SalesMetricResponseDTO> result = analyticsService.getSalesMetrics();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEventId()).isEqualTo(10L);
        assertThat(result.get(0).getTicketsSold()).isEqualTo(50);
        assertThat(result.get(0).getRevenue()).isEqualTo(500000.0);
    }

    @Test
    @DisplayName("saveSalesMetric - crea nueva métrica si no existe para ese eventId")
    void saveSalesMetric_createsNewWhenNotExists() {
        when(salesMetricRepository.findByEventId(10L)).thenReturn(Optional.empty());
        when(salesMetricRepository.save(any(SalesMetric.class))).thenReturn(sampleSales);

        SalesMetricRequestDTO request = SalesMetricRequestDTO.builder()
                .eventId(10L)
                .ticketsSold(50)
                .revenue(500000.0)
                .build();

        SalesMetricResponseDTO result = analyticsService.saveSalesMetric(request);

        assertThat(result.getEventId()).isEqualTo(10L);
        verify(salesMetricRepository).save(any(SalesMetric.class));
    }

    @Test
    @DisplayName("saveSalesMetric - acumula sobre métrica existente (upsert)")
    void saveSalesMetric_accumulatesWhenExists() {
        SalesMetric existing = SalesMetric.builder()
                .id(1L).eventId(10L).ticketsSold(20).revenue(200000.0).build();

        SalesMetric updated = SalesMetric.builder()
                .id(1L).eventId(10L).ticketsSold(70).revenue(700000.0).build();

        when(salesMetricRepository.findByEventId(10L)).thenReturn(Optional.of(existing));
        when(salesMetricRepository.save(any(SalesMetric.class))).thenReturn(updated);

        SalesMetricRequestDTO request = SalesMetricRequestDTO.builder()
                .eventId(10L).ticketsSold(50).revenue(500000.0).build();

        SalesMetricResponseDTO result = analyticsService.saveSalesMetric(request);

        // El service debe haber sumado 20+50=70 y 200000+500000=700000
        verify(salesMetricRepository).save(argThat(m ->
                m.getTicketsSold() == 70 && m.getRevenue() == 700000.0
        ));
    }

    // ── Payments ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getPaymentMetrics - retorna lista mapeada a DTOs")
    void getPaymentMetrics_shouldReturnMappedList() {
        when(paymentMetricRepository.findAll()).thenReturn(List.of(samplePayment));

        List<PaymentMetricResponseDTO> result = analyticsService.getPaymentMetrics();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getApprovedCount()).isEqualTo(40);
        assertThat(result.get(0).getRejectedCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("savePaymentMetric - persiste y retorna DTO")
    void savePaymentMetric_shouldPersistAndReturn() {
        when(paymentMetricRepository.save(any(PaymentMetric.class))).thenReturn(samplePayment);

        PaymentMetricRequestDTO request = PaymentMetricRequestDTO.builder()
                .approvedCount(40).rejectedCount(5).pendingCount(5).build();

        PaymentMetricResponseDTO result = analyticsService.savePaymentMetric(request);

        assertThat(result.getApprovedCount()).isEqualTo(40);
        verify(paymentMetricRepository).save(any(PaymentMetric.class));
    }

    // ── recordPaymentEvent ────────────────────────────────────────────────────

    @Test
    @DisplayName("recordPaymentEvent APPROVED - suma ventas y registra pago aprobado")
    void recordPaymentEvent_approved_accumulatesSales() {
        when(salesMetricRepository.findByEventId(10L)).thenReturn(Optional.empty());
        when(salesMetricRepository.save(any())).thenReturn(sampleSales);
        when(paymentMetricRepository.save(any())).thenReturn(samplePayment);

        analyticsService.recordPaymentEvent("APPROVED", 10L, 5, 50000.0);

        verify(salesMetricRepository).save(argThat(m ->
                m.getTicketsSold() == 5 && m.getRevenue() == 50000.0
        ));
        verify(paymentMetricRepository).save(argThat(m ->
                m.getApprovedCount() == 1 && m.getRejectedCount() == 0
        ));
    }

    @Test
    @DisplayName("recordPaymentEvent REJECTED - no suma ventas pero registra pago rechazado")
    void recordPaymentEvent_rejected_noSales() {
        when(salesMetricRepository.findByEventId(10L)).thenReturn(Optional.empty());
        when(salesMetricRepository.save(any())).thenReturn(sampleSales);
        when(paymentMetricRepository.save(any())).thenReturn(samplePayment);

        analyticsService.recordPaymentEvent("REJECTED", 10L, 5, 50000.0);

        verify(salesMetricRepository).save(argThat(m ->
                m.getTicketsSold() == 0 && m.getRevenue() == 0.0
        ));
        verify(paymentMetricRepository).save(argThat(m ->
                m.getRejectedCount() == 1 && m.getApprovedCount() == 0
        ));
    }
}
