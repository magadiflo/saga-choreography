package dev.magadiflo.order.app.controller;

import dev.magadiflo.order.app.dto.request.CreateOrderRequest;
import dev.magadiflo.order.app.dto.response.OrderResponse;
import dev.magadiflo.order.app.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller para gestionar órdenes.
 * <p>
 * Endpoints:
 * - POST /api/v1/orders: Crea una nueva orden
 * - GET /api/v1/orders/{orderCode}: Consulta el estado de una orden
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * Consulta el estado actual de una orden.
     * <p>
     * El cliente usa este endpoint para verificar el estado de la orden
     * después de haberla creado, ya que el procesamiento es asíncrono.
     * <p>
     * Estados posibles:
     * - PENDING: Orden creada, esperando procesamiento
     * - PAYMENT_CONFIRMED: Pago confirmado
     * - COMPLETED: Orden completada exitosamente
     * - CANCELLED: Orden cancelada (por fallo en pago o inventario)
     *
     * @param orderCode Identificador de negocio de la orden (ej: ORD-20260102180136-794B1F34)
     * @return ResponseEntity con OrderResponse y status 200
     */
    @GetMapping(path = "/{orderCode}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderCode) {
        log.info("Recuperar la orden con orderCode: {}", orderCode);
        return ResponseEntity.ok(this.orderService.getOrder(orderCode));
    }

    /**
     * Crea una nueva orden.
     * <p>
     * Este endpoint:
     * 1. Recibe la solicitud de creación de orden
     * 2. Se valida los datos (@Valid)
     * 3. Crea la orden en BD con estado PENDING
     * 4. Publica evento ORDER_CREATED a Kafka
     * 5. Retorna 202 ACCEPTED con la orden creada
     * <p>
     * Retorna 202 (Accepted) porque la orden se procesa ASÍNCRONAMENTE.
     * El cliente debe consultar el estado usando GET /api/v1/orders/{orderCode}
     *
     * @param request Datos de la orden a crear
     * @return ResponseEntity con OrderResponse y status 202
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("Se recibió una solicitud de creación de orden para el cliente: {}", request.customerCode());
        OrderResponse orderResponse = this.orderService.createOrder(request);
        log.info("Orden creada exitosamente: {}", orderResponse.orderCode());
        // 202 ACCEPTED: La solicitud fue aceptada pero se procesa asíncronamente
        return ResponseEntity.accepted().body(orderResponse);
    }

}
