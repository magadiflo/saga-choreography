package dev.magadiflo.order.app.service;

import dev.magadiflo.commons.event.InventoryReservedEvent;
import dev.magadiflo.commons.event.PaymentFailedEvent;
import dev.magadiflo.commons.event.PaymentProcessedEvent;
import dev.magadiflo.commons.event.PaymentRefundedEvent;
import dev.magadiflo.order.app.dto.request.CreateOrderRequest;
import dev.magadiflo.order.app.dto.response.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);

    OrderResponse getOrder(String orderCode);

    void handlePaymentProcessed(PaymentProcessedEvent event);

    void handlePaymentFailed(PaymentFailedEvent event);

    void handlePaymentRefunded(PaymentRefundedEvent event);

    void handleInventoryReserved(InventoryReservedEvent event);
}
