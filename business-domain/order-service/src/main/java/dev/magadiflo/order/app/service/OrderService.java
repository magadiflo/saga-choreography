package dev.magadiflo.order.app.service;

import dev.magadiflo.order.app.dto.event.InventoryReservedEvent;
import dev.magadiflo.order.app.dto.event.PaymentFailedEvent;
import dev.magadiflo.order.app.dto.event.PaymentProcessedEvent;
import dev.magadiflo.order.app.dto.event.PaymentRefundedEvent;
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
