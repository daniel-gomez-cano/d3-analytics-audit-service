package co.empresa.proyecto_desarrollo3.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMetricRequestDTO {

    @PositiveOrZero(message = "approvedCount must be >= 0")
    private Integer approvedCount;

    @PositiveOrZero(message = "rejectedCount must be >= 0")
    private Integer rejectedCount;

    @PositiveOrZero(message = "pendingCount must be >= 0")
    private Integer pendingCount;
}
