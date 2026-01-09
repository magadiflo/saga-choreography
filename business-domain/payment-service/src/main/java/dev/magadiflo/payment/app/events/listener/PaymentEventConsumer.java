package dev.magadiflo.payment.app.events.listener;

import dev.magadiflo.commons.constants.OrderMessagingConstants;
import dev.magadiflo.commons.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentEventConsumer {
    @KafkaListener(
            topics = OrderMessagingConstants.TOPIC_ORDER_CREATED, //Le dice a Kafka qué mensajes leer
            groupId = "${spring.kafka.consumer.group-id}" //Permite el balanceo de carga y seguimiento de offsets.
    )
    public void consumeOrderCreated(OrderCreatedEvent event) {
        log.info("Iniciando procesamiento de pago para la orden: {}", event);

    }

}
