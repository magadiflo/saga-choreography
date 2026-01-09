package dev.magadiflo.order.app.events.publisher;

import dev.magadiflo.commons.constants.OrderMessagingConstants;
import dev.magadiflo.commons.event.OrderCreatedEvent;
import dev.magadiflo.order.app.entity.Order;
import dev.magadiflo.order.app.model.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderEventPublisher {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    /**
     * Publica el evento ORDER_CREATED a Kafka.
     * Este evento inicia el flujo SAGA.
     *
     * @param order Orden creada
     */
    public void publishOrderCreatedEvent(Order order) {
        // Construir la lista de items para el evento
        List<OrderCreatedEvent.OrderItem> eventItems = order.getOrderDetails().stream()
                .map(orderDetail -> OrderCreatedEvent.OrderItem.builder()
                        .productCode(orderDetail.getProductCode())
                        .quantity(orderDetail.getQuantity())
                        .price(orderDetail.getPrice())
                        .build()
                ).toList();

        // Construir el payload
        OrderCreatedEvent.Payload payload = OrderCreatedEvent.Payload.builder()
                .customerCode(order.getCustomerCode())
                .totalAmount(order.getTotalAmount())
                .currency(order.getCurrency())
                .items(eventItems)
                .build();

        // Construir evento completo
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(EventType.ORDER_CREATED.name())
                .timestamp(LocalDateTime.now())
                .orderCode(order.getOrderCode())
                .payload(payload)
                .build();

        // Publicar a Kafka usando orderCode como key (cada order irá a una misma partición según su orderCode)
        this.kafkaTemplate.send(OrderMessagingConstants.TOPIC_ORDER_CREATED, order.getOrderCode(), event);
        log.info("Se publicó el evento {}, en el topic {}, para la orden {}",
                EventType.ORDER_CREATED.name(), OrderMessagingConstants.TOPIC_ORDER_CREATED, order.getOrderCode());
    }
}
