package co.empresa.proyecto_desarrollo3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "entityId is required")
    private Long entityId;

    private Long userId;

    private String payload;
}
