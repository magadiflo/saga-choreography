package dev.magadiflo.order.app.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String orderCode) {
        super("Orden no encontrada: " + orderCode);
    }
}
