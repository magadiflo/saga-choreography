package dev.magadiflo.payment.app.model;

/**
 * Estados posibles de un pago en el flujo SAGA.
 * <p>
 * Transiciones:
 * - PENDING → PROCESSED (happy path)
 * - PENDING → FAILED (payment falló)
 * - PROCESSED → REFUNDED (compensación cuando inventory falla)
 */
public enum PaymentStatus {
    /**
     * Pago en proceso.
     * Estado inicial al recibir ORDER_CREATED.
     */
    PENDING,

    /**
     * Pago procesado exitosamente.
     * Se publica evento PAYMENT_PROCESSED.
     */
    PROCESSED,

    /**
     * Pago falló.
     * Se publica evento PAYMENT_FAILED.
     */
    FAILED,

    /**
     * Pago reembolsado (compensación).
     * Ocurre cuando Inventory Service reporta fallo.
     * Se publica evento PAYMENT_REFUNDED.
     */
    REFUNDED
}
