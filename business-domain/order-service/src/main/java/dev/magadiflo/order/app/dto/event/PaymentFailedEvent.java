package dev.magadiflo.order.app.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class PaymentFailedEvent extends BaseEvent {

    private Payload payload;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class Payload {
        private String reason;
        private String errorCode;
    }
}
