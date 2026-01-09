package dev.magadiflo.payment.app.model;

public enum PaymentStatus {
    PENDING,     // Pago en proceso
    PROCESSED,   // Pago exitoso
    FAILED,      // Pago falló
    REFUNDED     // Pago reembolsado (compensación)
}
