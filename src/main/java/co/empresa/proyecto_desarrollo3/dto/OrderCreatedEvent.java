package co.empresa.proyecto_desarrollo3.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreatedEvent {

    private String cartId;
    private String buyerId;
    private String buyerEmail;
    private List<OrderItem> items;
    private BigDecimal total;
    private String discountCode;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItem {

        private String ticketTypeId;
        private String ticketTypeName;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
    }
}