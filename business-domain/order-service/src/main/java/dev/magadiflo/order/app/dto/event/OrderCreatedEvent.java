package dev.magadiflo.order.app.dto.event;

import dev.magadiflo.order.app.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class OrderCreatedEvent extends BaseEvent {

    private Payload payload;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class Payload {
        private String customerId;
        private BigDecimal totalAmount;
        private Currency currency;
        private List<OrderItem> items;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class OrderItem {
        private String productId;
        private Integer quantity;
        private BigDecimal price;
    }

}
