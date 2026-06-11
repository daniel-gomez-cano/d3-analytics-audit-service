package co.empresa.proyecto_desarrollo3.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResultEvent {

    private String cartId;

    private String paymentId;

    private String mercadoPagoPaymentId;

    private String buyerId;

    private String status;

    private BigDecimal amount;

    private String statusDetail;

    private LocalDateTime processedAt;
}