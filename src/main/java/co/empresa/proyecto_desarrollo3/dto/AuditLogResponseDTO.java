package co.empresa.proyecto_desarrollo3.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponseDTO {

    private Long id;
    private String eventType;
    private String entityType;
    private Long entityId;
    private Long userId;
    private String payload;
    private LocalDateTime createdAt;
}
