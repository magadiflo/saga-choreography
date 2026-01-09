package dev.magadiflo.commons.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class PaymentRefundedEvent extends BaseEvent {

    private Payload payload;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class Payload {
        private String refundCode;
        private BigDecimal amount;
        private String originalPaymentCode;
        private String reason;
    }
}
