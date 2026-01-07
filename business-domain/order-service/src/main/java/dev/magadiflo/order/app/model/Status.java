package dev.magadiflo.order.app.model;

public enum Status {
    PENDING,            // Orden creada, esperando tod el flujo
    PAYMENT_CONFIRMED,  // Pago confirmado
    COMPLETED,          // Happy path final
    CANCELLED           // Falló algo
}
