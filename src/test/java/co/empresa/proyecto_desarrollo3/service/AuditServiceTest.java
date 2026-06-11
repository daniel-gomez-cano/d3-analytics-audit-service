package co.empresa.proyecto_desarrollo3.service;

import co.empresa.proyecto_desarrollo3.dto.AuditLogRequestDTO;
import co.empresa.proyecto_desarrollo3.dto.AuditLogResponseDTO;
import co.empresa.proyecto_desarrollo3.model.AuditLog;
import co.empresa.proyecto_desarrollo3.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditLogRepository repository;

    @InjectMocks
    private AuditService auditService;

    private AuditLog sampleLog;

    @BeforeEach
    void setUp() {
        sampleLog = AuditLog.builder()
                .id(1L)
                .eventType("ORDER_CONFIRMED")
                .entityType("ORDER")
                .entityId("100")
                .userId("5")
                .payload("{\"orderId\":100}")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("save - debe persistir el log y retornar el DTO con id")
    void save_shouldPersistAndReturnDTO() {
        when(repository.save(any(AuditLog.class))).thenReturn(sampleLog);

        AuditLogRequestDTO request = AuditLogRequestDTO.builder()
                .eventType("ORDER_CONFIRMED")
                .entityType("ORDER")
                .entityId("100")
                .userId("5")
                .payload("{\"orderId\":100}")
                .build();

        AuditLogResponseDTO response = auditService.save(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEventType()).isEqualTo("ORDER_CONFIRMED");
        assertThat(response.getEntityType()).isEqualTo("ORDER");
        assertThat(response.getEntityId()).isEqualTo("100");
        assertThat(response.getUserId()).isEqualTo("5");
        verify(repository, times(1)).save(any(AuditLog.class));
    }

    @Test
    @DisplayName("saveInternal - debe persistir sin pasar por DTO")
    void saveInternal_shouldPersist() {
        when(repository.save(any(AuditLog.class))).thenReturn(sampleLog);

        auditService.saveInternal("ORDER_CONFIRMED", "ORDER", "100", "5", "{\"orderId\":100}");

        verify(repository, times(1)).save(any(AuditLog.class));
    }

    @Test
    @DisplayName("getAllLogs - debe retornar lista mapeada a DTOs")
    void getAllLogs_shouldReturnMappedList() {
        when(repository.findAll()).thenReturn(List.of(sampleLog));

        List<AuditLogResponseDTO> result = auditService.getAllLogs();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEventType()).isEqualTo("ORDER_CONFIRMED");
    }

    @Test
    @DisplayName("getAllLogs - lista vacía si no hay registros")
    void getAllLogs_emptyWhenNoLogs() {
        when(repository.findAll()).thenReturn(List.of());

        List<AuditLogResponseDTO> result = auditService.getAllLogs();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getLogsByEntityType - filtra por entityType y entityId")
    void getLogsByEntityType_shouldFilter() {
        when(repository.findByEntityTypeAndEntityId("ORDER", "100"))
                .thenReturn(List.of(sampleLog));

        List<AuditLogResponseDTO> result = auditService.getLogsByEntityType("ORDER", "100");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEntityId()).isEqualTo("100");
    }

    @Test
    @DisplayName("getLogsByEventType - filtra por eventType")
    void getLogsByEventType_shouldFilter() {
        when(repository.findByEventType("ORDER_CONFIRMED"))
                .thenReturn(List.of(sampleLog));

        List<AuditLogResponseDTO> result = auditService.getLogsByEventType("ORDER_CONFIRMED");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEventType()).isEqualTo("ORDER_CONFIRMED");
    }
}
