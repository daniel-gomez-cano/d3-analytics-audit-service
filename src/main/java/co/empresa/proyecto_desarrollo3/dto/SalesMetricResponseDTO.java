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
public class SalesMetricResponseDTO {

    private Long id;
    private Long eventId;
    private Integer ticketsSold;
    private Double revenue;
    private LocalDateTime updatedAt;
}
