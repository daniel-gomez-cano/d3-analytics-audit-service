package co.empresa.proyecto_desarrollo3.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesMetricRequestDTO {

    @NotNull(message = "eventId is required")
    private Long eventId;

    @PositiveOrZero(message = "ticketsSold must be >= 0")
    private Integer ticketsSold;

    @PositiveOrZero(message = "revenue must be >= 0")
    private Double revenue;
}
