package dev.magadiflo.order.app.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public abstract class BaseEvent {
    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;
    private String orderId;

    protected void initializeBaseFields(String eventType, String orderId) {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.eventType = eventType;
        this.orderId = orderId;
    }
}
