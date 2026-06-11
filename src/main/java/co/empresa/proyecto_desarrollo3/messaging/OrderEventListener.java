package co.empresa.proyecto_desarrollo3.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.empresa.proyecto_desarrollo3.config.RabbitMQConfig;
import co.empresa.proyecto_desarrollo3.dto.PaymentResultEvent;
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

        log.info("ANTES DE GUARDAR AUDITORIA");


        try {

            String payload = objectMapper.writeValueAsString(event);

            auditService.saveInternal(
                    "ORDER_CREATED",
                    "ORDER",
                    event.getCartId(),
                    event.getBuyerId(),
                    payload
            );
        log.info("AUDITORIA GUARDADA");

        } catch (Exception e) {
            log.error("Error procesando evento: {}", e.getMessage());
        }
    }

    /**
     * Escucha resultados de pago desde el payment/order service.
     * Registra en auditoría y actualiza métricas de pagos.
     */
    @RabbitListener(queues = RabbitMQConfig.PAYMENT_RESULT_QUEUE)
    public void handlePaymentResult(PaymentResultEvent event) {
        log.info("Received payment event: cartId={}, paymentId={}, status={}", event.getCartId(), event.getPaymentId(), event.getStatus());
        try {
            String payload = objectMapper.writeValueAsString(event);
            auditService.saveInternal(
                    "PAYMENT_" + event.getStatus(),
                    "PAYMENT",
                    event.getCartId(),
                    event.getBuyerId(),
                    payload
            );

            auditService.saveInternal(
                "PAYMENT_" + event.getStatus(),
                "PAYMENT",
                event.getPaymentId(),
                event.getBuyerId(),
                payload
            );

        } catch (JsonProcessingException e) {
            log.error("Error serializing payment event payload: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error processing payment event cartId={}: {}", event.getCartId(), e.getMessage());
        }
    }
}
