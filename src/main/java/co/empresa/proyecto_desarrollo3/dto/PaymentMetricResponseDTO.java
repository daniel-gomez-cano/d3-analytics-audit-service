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
public class PaymentMetricResponseDTO {

    private Long id;
    private Integer approvedCount;
    private Integer rejectedCount;
    private Integer pendingCount;
    private LocalDateTime updatedAt;
}
