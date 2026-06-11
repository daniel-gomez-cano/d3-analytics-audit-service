package co.empresa.proyecto_desarrollo3.controller;

import co.empresa.proyecto_desarrollo3.dto.PaymentMetricRequestDTO;
import co.empresa.proyecto_desarrollo3.dto.PaymentMetricResponseDTO;
import co.empresa.proyecto_desarrollo3.dto.SalesMetricRequestDTO;
import co.empresa.proyecto_desarrollo3.dto.SalesMetricResponseDTO;
import co.empresa.proyecto_desarrollo3.service.AnalyticsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnalyticsController.class)
@AutoConfigureMockMvc(addFilters = false)
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    private ObjectMapper objectMapper;
    private SalesMetricResponseDTO sampleSales;
    private PaymentMetricResponseDTO samplePayment;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        sampleSales = SalesMetricResponseDTO.builder()
                .id(1L)
                .eventId(10L)
                .ticketsSold(50)
                .revenue(500000.0)
                .updatedAt(LocalDateTime.now())
                .build();

        samplePayment = PaymentMetricResponseDTO.builder()
                .id(1L)
                .approvedCount(40)
                .rejectedCount(5)
                .pendingCount(5)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ── Sales ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /analytics/sales - retorna lista de métricas de ventas")
    @WithMockUser(roles = "ADMIN")
    void getSalesMetrics_shouldReturnList() throws Exception {
        when(analyticsService.getSalesMetrics()).thenReturn(List.of(sampleSales));

        mockMvc.perform(get("/analytics/sales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].eventId").value(10))
                .andExpect(jsonPath("$[0].ticketsSold").value(50))
                .andExpect(jsonPath("$[0].revenue").value(500000.0));
    }

    @Test
    @DisplayName("GET /analytics/sales - lista vacía cuando no hay métricas")
    @WithMockUser(roles = "ADMIN")
    void getSalesMetrics_emptyList() throws Exception {
        when(analyticsService.getSalesMetrics()).thenReturn(List.of());

        mockMvc.perform(get("/analytics/sales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("POST /analytics/sales - crea métrica y retorna 201")
    @WithMockUser(roles = "ADMIN")
    void createSalesMetric_shouldReturn201() throws Exception {
        when(analyticsService.saveSalesMetric(any(SalesMetricRequestDTO.class))).thenReturn(sampleSales);

        SalesMetricRequestDTO request = SalesMetricRequestDTO.builder()
                .eventId(10L)
                .ticketsSold(50)
                .revenue(500000.0)
                .build();

        mockMvc.perform(post("/analytics/sales")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventId").value(10))
                .andExpect(jsonPath("$.ticketsSold").value(50));
    }

    @Test
    @DisplayName("POST /analytics/sales - retorna 400 si falta eventId")
    @WithMockUser(roles = "ADMIN")
    void createSalesMetric_missingEventId_returns400() throws Exception {
        SalesMetricRequestDTO request = SalesMetricRequestDTO.builder()
                .ticketsSold(50)
                .revenue(500000.0)
                .build();

        mockMvc.perform(post("/analytics/sales")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ── Payments ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /analytics/payments - retorna lista de métricas de pagos")
    @WithMockUser(roles = "ADMIN")
    void getPaymentMetrics_shouldReturnList() throws Exception {
        when(analyticsService.getPaymentMetrics()).thenReturn(List.of(samplePayment));

        mockMvc.perform(get("/analytics/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].approvedCount").value(40))
                .andExpect(jsonPath("$[0].rejectedCount").value(5))
                .andExpect(jsonPath("$[0].pendingCount").value(5));
    }

    @Test
    @DisplayName("POST /analytics/payments - crea métrica y retorna 201")
    @WithMockUser(roles = "ADMIN")
    void createPaymentMetric_shouldReturn201() throws Exception {
        when(analyticsService.savePaymentMetric(any(PaymentMetricRequestDTO.class))).thenReturn(samplePayment);

        PaymentMetricRequestDTO request = PaymentMetricRequestDTO.builder()
                .approvedCount(40)
                .rejectedCount(5)
                .pendingCount(5)
                .build();

        mockMvc.perform(post("/analytics/payments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.approvedCount").value(40));
    }

    @Test
    @DisplayName("POST /analytics/payments - retorna 400 si approvedCount es negativo")
    @WithMockUser(roles = "ADMIN")
    void createPaymentMetric_negativeCount_returns400() throws Exception {
        PaymentMetricRequestDTO request = PaymentMetricRequestDTO.builder()
                .approvedCount(-1)
                .rejectedCount(5)
                .pendingCount(5)
                .build();

        mockMvc.perform(post("/analytics/payments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
