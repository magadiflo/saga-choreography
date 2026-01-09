package dev.magadiflo.payment.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del Payment Service.
 *
 * Este microservicio es responsable de:
 * - Procesar pagos cuando recibe eventos ORDER_CREATED
 * - Publicar eventos PAYMENT_PROCESSED o PAYMENT_FAILED
 * - Ejecutar compensaciones (refunds) cuando recibe INVENTORY_FAILED
 * - Publicar eventos PAYMENT_REFUNDED al completar la compensación
 */
@SpringBootApplication
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }

}
