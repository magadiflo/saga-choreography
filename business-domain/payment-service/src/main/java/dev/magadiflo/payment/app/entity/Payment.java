package dev.magadiflo.payment.app.entity;

import dev.magadiflo.commons.enums.Currency;
import dev.magadiflo.payment.app.model.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad Payment - Representa un pago en el sistema.
 * <p>
 * Almacena el estado del pago a lo largo del flujo SAGA.
 * Esta entidad se crea cuando Payment Service recibe un evento ORDER_CREATED
 * y se actualiza según el resultado del procesamiento.
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "payments")
public class Payment {
    /**
     * ID técnico - Clave primaria interna.
     * Auto-incremental para eficiencia en índices.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Payment Code - Clave de negocio externa.
     * Identificador único del pago expuesto en eventos y logs.
     * Formato: PAY-{timestamp}-{random} (ej: PAY-1735680000-B7D2)
     */
    @Column(unique = true, nullable = false, length = 50)
    private String paymentCode;

    /**
     * Order Code - Referencia a la orden asociada.
     * NO es FK porque Order está en otra base de datos (Order Service).
     * Se usa para vincular el pago con su orden en eventos.
     */
    @Column(nullable = false, length = 50)
    private String orderCode;

    /**
     * Monto del pago.
     * Debe coincidir con el totalAmount de la orden.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * Moneda del pago.
     * Debe coincidir con la moneda de la orden.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private Currency currency;

    /**
     * Estado actual del pago.
     * Se actualiza según el flujo SAGA.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    /**
     * Transaction Code - Identificador de la transacción externa.
     * En un sistema real, sería el ID del gateway de pago (Stripe, PayPal, etc.)
     * En nuestra simulación, lo generamos nosotros.
     * Formato: TXN-{random}
     */
    @Column(length = 100)
    private String transactionCode;

    /**
     * Razón del fallo o reembolso (si aplica).
     * Se llena cuando status = FAILED o REFUNDED.
     */
    @Column(length = 200)
    private String failureReason;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(this.getPaymentCode(), payment.getPaymentCode());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getPaymentCode());
    }
}
