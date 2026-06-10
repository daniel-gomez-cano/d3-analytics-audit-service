package co.empresa.proyecto_desarrollo3.controller;

import co.empresa.proyecto_desarrollo3.dto.AuditLogRequestDTO;
import co.empresa.proyecto_desarrollo3.dto.AuditLogResponseDTO;
import co.empresa.proyecto_desarrollo3.service.AuditService;
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

@WebMvcTest(AuditController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditService auditService;

    private ObjectMapper objectMapper;
    private AuditLogResponseDTO sampleResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        sampleResponse = AuditLogResponseDTO.builder()
                .id(1L)
                .eventType("ORDER_CONFIRMED")
                .entityType("ORDER")
                .entityId(100L)
                .userId(5L)
                .payload("{\"orderId\":100}")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("GET /audit/logs - retorna lista de logs")
    @WithMockUser(roles = "ADMIN")
    void getLogs_shouldReturnList() throws Exception {
        when(auditService.getAllLogs()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/audit/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].eventType").value("ORDER_CONFIRMED"))
                .andExpect(jsonPath("$[0].entityType").value("ORDER"))
                .andExpect(jsonPath("$[0].entityId").value(100));
    }

    @Test
    @DisplayName("GET /audit/logs - lista vacía cuando no hay registros")
    @WithMockUser(roles = "ADMIN")
    void getLogs_emptyList() throws Exception {
        when(auditService.getAllLogs()).thenReturn(List.of());

        mockMvc.perform(get("/audit/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("POST /audit/logs - crea log y retorna 201")
    @WithMockUser(roles = "ADMIN")
    void createLog_shouldReturn201() throws Exception {
        when(auditService.save(any(AuditLogRequestDTO.class))).thenReturn(sampleResponse);

        AuditLogRequestDTO request = AuditLogRequestDTO.builder()
                .eventType("ORDER_CONFIRMED")
                .entityType("ORDER")
                .entityId(100L)
                .userId(5L)
                .payload("{\"orderId\":100}")
                .build();

        mockMvc.perform(post("/audit/logs")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.eventType").value("ORDER_CONFIRMED"));
    }

    @Test
    @DisplayName("POST /audit/logs - retorna 400 si falta eventType")
    @WithMockUser(roles = "ADMIN")
    void createLog_missingEventType_returns400() throws Exception {
        AuditLogRequestDTO request = AuditLogRequestDTO.builder()
                .entityType("ORDER")
                .entityId(100L)
                .build();

        mockMvc.perform(post("/audit/logs")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /audit/logs - retorna 400 si falta entityType")
    @WithMockUser(roles = "ADMIN")
    void createLog_missingEntityType_returns400() throws Exception {
        AuditLogRequestDTO request = AuditLogRequestDTO.builder()
                .eventType("ORDER_CONFIRMED")
                .entityId(100L)
                .build();

        mockMvc.perform(post("/audit/logs")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /audit/logs/entity/{entityType}/{entityId} - filtra por entidad")
    @WithMockUser(roles = "ADMIN")
    void getLogsByEntity_shouldFilter() throws Exception {
        when(auditService.getLogsByEntityType("ORDER", 100L)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/audit/logs/entity/ORDER/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].entityId").value(100));
    }

    @Test
    @DisplayName("GET /audit/logs/event/{eventType} - filtra por tipo de evento")
    @WithMockUser(roles = "ADMIN")
    void getLogsByEventType_shouldFilter() throws Exception {
        when(auditService.getLogsByEventType("ORDER_CONFIRMED")).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/audit/logs/event/ORDER_CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventType").value("ORDER_CONFIRMED"));
    }
}
