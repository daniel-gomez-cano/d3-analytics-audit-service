package co.empresa.proyecto_desarrollo3.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Evento recibido desde d3-order-service o ticket-service via RabbitMQ.
 * Representa una orden/pago completado que dispara registro de métricas y auditoría.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEventDTO {

    private Long orderId;
    private Long eventId;
    private Long userId;
    private String status;        // CONFIRMED, FAILED, PENDING
    private Integer ticketCount;
    private Double totalAmount;
    private String paymentStatus; // APPROVED, REJECTED, PENDING
    private LocalDateTime occurredAt;
    private String description;
}
