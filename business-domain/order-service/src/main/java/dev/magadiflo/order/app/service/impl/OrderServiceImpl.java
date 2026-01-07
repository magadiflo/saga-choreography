package dev.magadiflo.order.app.service.impl;

import dev.magadiflo.order.app.dto.event.InventoryReservedEvent;
import dev.magadiflo.order.app.dto.event.PaymentFailedEvent;
import dev.magadiflo.order.app.dto.event.PaymentProcessedEvent;
import dev.magadiflo.order.app.dto.event.PaymentRefundedEvent;
import dev.magadiflo.order.app.dto.request.CreateOrderRequest;
import dev.magadiflo.order.app.dto.request.OrderDetailRequest;
import dev.magadiflo.order.app.dto.response.OrderResponse;
import dev.magadiflo.order.app.entity.Order;
import dev.magadiflo.order.app.events.publisher.OrderEventPublisher;
import dev.magadiflo.order.app.exception.OrderNotFoundException;
import dev.magadiflo.order.app.mapper.OrderMapper;
import dev.magadiflo.order.app.model.Status;
import dev.magadiflo.order.app.repository.OrderRepository;
import dev.magadiflo.order.app.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Servicio que gestiona la lógica de negocio de las órdenes.
 * <p>
 * Responsabilidades:
 * - Crear órdenes y publicar eventos ORDER_CREATED
 * - Consultar estado de órdenes
 * - Actualizar estado según eventos recibidos de otros servicios
 * - Coordinar el flujo SAGA mediante eventos Kafka
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    /**
     * Crea una nueva orden y publica el evento ORDER_CREATED.
     * <p>
     * Flujo:
     * 1. Genera orderCode único
     * 2. Calcula total amount
     * 3. Crea entidad Order con detalles
     * 4. Persiste en BD con estado PENDING
     * 5. Publica evento order.created
     * 6. Retorna OrderResponse
     *
     * @param request Solicitud con datos de la orden
     * @return OrderResponse con orderCode y estado PENDING
     */
    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creando order para cliente: {}", request.customerCode());

        // 1. Generar código de negocio único
        String orderCode = this.generateOrderCode();

        // 2. Calcular el monto total (suma de subtotal = price * quantity de cada item)
        BigDecimal totalAmount = request.items().stream()
                .map(OrderDetailRequest::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Crear la entidad Order con detalles
        Order order = OrderMapper.toCreateEntityOrder(request, orderCode, totalAmount);

        // 4. Persiste en BD con estado PENDING
        Order savedOrder = this.orderRepository.save(order);
        log.info("Orden creado con ID: {} | con orderCode: {}", savedOrder.getId(), savedOrder.getOrderCode());

        // 5. Publica evento order.created
        this.orderEventPublisher.publishOrderCreatedEvent(savedOrder);

        // 6. Retorna OrderResponse
        return OrderMapper.toOrderResponse(savedOrder);
    }

    /**
     * Consulta una orden por su orderCode.
     *
     * @param orderCode Identificador de negocio de la orden
     * @return OrderResponse con el estado actual de la orden
     * @throws RuntimeException si la orden no existe
     */
    @Override
    public OrderResponse getOrder(String orderCode) {
        log.info("Recuperando orden: {}", orderCode);
        return this.orderRepository.findByOrderCode(orderCode)
                .map(OrderMapper::toOrderResponse)
                .orElseThrow(() -> new OrderNotFoundException(orderCode));
    }

    /**
     * Actualiza el estado de la orden a PAYMENT_CONFIRMED.
     * Se ejecuta cuando se recibe el evento PaymentProcessedEvent.
     *
     * @param event Evento de pago procesado
     */
    @Override
    @Transactional
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        log.info("Procesando el evento de pago realizado para la orden: {}", event.getOrderCode());
        Order order = this.orderRepository.findByOrderCode(event.getOrderCode())
                .map(orderDB -> {
                    orderDB.setStatus(Status.PAYMENT_CONFIRMED);
                    return orderDB;
                })
                .orElseThrow(() -> new OrderNotFoundException(event.getOrderCode()));
        this.orderRepository.save(order);
        log.info("Orden {} actualizada a {}", event.getOrderCode(), Status.PAYMENT_CONFIRMED);
    }

    /**
     * Actualiza el estado de la orden a CANCELLED.
     * Se ejecuta cuando se recibe el evento PaymentFailedEvent.
     *
     * @param event Evento de pago fallido
     */
    @Override
    @Transactional
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.info("Manejo de eventos de pago fallidos para la orden: {}", event.getOrderCode());
        Order order = this.orderRepository.findByOrderCode(event.getOrderCode())
                .map(orderDB -> {
                    orderDB.setStatus(Status.CANCELLED);
                    return orderDB;
                })
                .orElseThrow(() -> new OrderNotFoundException(event.getOrderCode()));
        this.orderRepository.save(order);
        log.info("Orden {} cancelada debido a fallo en el pago: {}", event.getOrderCode(), event.getPayload().getReason());
    }

    /**
     * Actualiza el estado de la orden a CANCELLED.
     * Se ejecuta cuando se recibe el evento PaymentRefundedEvent.
     * Este es el FINAL del flujo de COMPENSACIÓN.
     *
     * @param event Evento de pago reembolsado
     */
    @Override
    @Transactional
    public void handlePaymentRefunded(PaymentRefundedEvent event) {
        log.info("Manejo de eventos de reembolso de pago para la orden {}", event.getOrderCode());
        Order order = this.orderRepository.findByOrderCode(event.getOrderCode())
                .map(orderDB -> {
                    orderDB.setStatus(Status.CANCELLED);
                    return orderDB;
                })
                .orElseThrow(() -> new OrderNotFoundException(event.getOrderCode()));
        this.orderRepository.save(order);
        log.info("Orden {} {} tras reembolso. Motivo: {}", event.getOrderCode(), Status.CANCELLED, event.getPayload().getReason());
    }

    /**
     * Actualiza el estado de la orden a COMPLETED.
     * Se ejecuta cuando se recibe el evento InventoryReservedEvent.
     * Este es el FINAL EXITOSO del flujo SAGA.
     *
     * @param event Evento de inventario reservado
     */
    @Override
    @Transactional
    public void handleInventoryReserved(InventoryReservedEvent event) {
        log.info("Manejo de evento reservado de inventario para la orden {}", event.getOrderCode());
        Order order = this.orderRepository.findByOrderCode(event.getOrderCode())
                .map(orderDB -> {
                    orderDB.setStatus(Status.COMPLETED);
                    return orderDB;
                })
                .orElseThrow(() -> new OrderNotFoundException(event.getOrderCode()));
        this.orderRepository.save(order);
        log.info("Orden {} {} exitosamente", event.getOrderCode(), Status.COMPLETED);
    }

    /**
     * Genera un orderCode único.
     * Formato: ORD-{yyyyMMddHHmmss}-{shortRandom}
     * Ejemplo: ORD-20260102180136-794B1F34
     */
    private String generateOrderCode() {
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String shortRandom = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
        return "ORD-%s-%s".formatted(dateTime, shortRandom);
    }
}
