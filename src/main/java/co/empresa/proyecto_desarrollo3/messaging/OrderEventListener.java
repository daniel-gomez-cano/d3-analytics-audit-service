package co.empresa.proyecto_desarrollo3.messaging;

import co.empresa.proyecto_desarrollo3.config.RabbitMQConfig;
import co.empresa.proyecto_desarrollo3.dto.OrderEventDTO;
import co.empresa.proyecto_desarrollo3.service.AnalyticsService;
import co.empresa.proyecto_desarrollo3.service.AuditService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final AnalyticsService analyticsService;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    /**
     * Escucha órdenes confirmadas desde d3-order-service.
     * Registra en auditoría y acumula métricas de ventas.
     */
    @RabbitListener(queues = RabbitMQConfig.ANALYTICS_ORDER_QUEUE)
    public void handleOrderConfirmed(OrderEventDTO event) {
        log.info("Received order event: orderId={}, status={}", event.getOrderId(), event.getStatus());
        try {
            // 1. Registrar en auditoría
            String payload = objectMapper.writeValueAsString(event);
            auditService.saveInternal(
                    "ORDER_" + event.getStatus(),
                    "ORDER",
                    event.getOrderId(),
                    event.getUserId(),
                    payload
            );

            // 2. Si la orden es CONFIRMED, acumular métricas de ventas
            if ("CONFIRMED".equalsIgnoreCase(event.getStatus())) {
                analyticsService.recordPaymentEvent(
                        event.getPaymentStatus() != null ? event.getPaymentStatus() : "APPROVED",
                        event.getEventId(),
                        event.getTicketCount(),
                        event.getTotalAmount()
                );
            }

        } catch (JsonProcessingException e) {
            log.error("Error serializing order event payload: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error processing order event orderId={}: {}", event.getOrderId(), e.getMessage());
        }
    }

    /**
     * Escucha resultados de pago desde el payment/order service.
     * Registra en auditoría y actualiza métricas de pagos.
     */
    @RabbitListener(queues = RabbitMQConfig.ANALYTICS_PAYMENT_QUEUE)
    public void handlePaymentResult(OrderEventDTO event) {
        log.info("Received payment event: orderId={}, paymentStatus={}", event.getOrderId(), event.getPaymentStatus());
        try {
            String payload = objectMapper.writeValueAsString(event);
            auditService.saveInternal(
                    "PAYMENT_" + event.getPaymentStatus(),
                    "PAYMENT",
                    event.getOrderId(),
                    event.getUserId(),
                    payload
            );

            analyticsService.recordPaymentEvent(
                    event.getPaymentStatus(),
                    event.getEventId(),
                    event.getTicketCount(),
                    event.getTotalAmount()
            );

        } catch (JsonProcessingException e) {
            log.error("Error serializing payment event payload: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error processing payment event orderId={}: {}", event.getOrderId(), e.getMessage());
        }
    }
}
