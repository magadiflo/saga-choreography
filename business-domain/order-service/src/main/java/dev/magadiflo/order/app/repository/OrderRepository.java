package dev.magadiflo.order.app.repository;

import dev.magadiflo.order.app.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderId(String orderId);

    boolean existsByOrderId(String orderId);
}
