package co.empresa.proyecto_desarrollo3.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.empresa.proyecto_desarrollo3.config.RabbitMQConfig;
import co.empresa.proyecto_desarrollo3.dto.OrderEventDTO;
import co.empresa.proyecto_desarrollo3.dto.OrderCreatedEvent;
import co.empresa.proyecto_desarrollo3.service.AnalyticsService;
import co.empresa.proyecto_desarrollo3.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    @RabbitListener(queues = RabbitMQConfig.ORDER_CREATED_QUEUE)
    public void handleOrderConfirmed(OrderCreatedEvent event) {

        log.info(
                "ORDER RECIBIDA -> cartId={} buyerId={} total={}",
                event.getCartId(),
                event.getBuyerId(),
                event.getTotal()
        );

        try {

            String payload = objectMapper.writeValueAsString(event);

            auditService.saveInternal(
                    "ORDER_CREATED",
                    "ORDER",
                    1L,
                    1L,
                    payload
            );

        } catch (Exception e) {
            log.error("Error procesando evento: {}", e.getMessage());
        }
    }

    /**
     * Escucha resultados de pago desde el payment/order service.
     * Registra en auditoría y actualiza métricas de pagos.
     */
    @RabbitListener(queues = RabbitMQConfig.PAYMENT_RESULT_QUEUE)
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
