package dev.magadiflo.payment.app.repository;

import dev.magadiflo.payment.app.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentCode(String paymentCode);
}
