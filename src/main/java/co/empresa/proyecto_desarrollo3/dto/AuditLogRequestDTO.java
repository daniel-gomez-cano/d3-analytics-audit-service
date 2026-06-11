package co.empresa.proyecto_desarrollo3.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogRequestDTO {

    @NotBlank(message = "eventType is required")
    private String eventType;

    @NotBlank(message = "entityType is required")
    private String entityType;

    @NotBlank(message = "entityId is required")
    private String entityId;

    private String userId;

    private String payload;
}
