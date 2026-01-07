package dev.magadiflo.order.app.mapper;

import dev.magadiflo.order.app.dto.request.CreateOrderRequest;
import dev.magadiflo.order.app.dto.request.OrderDetailRequest;
import dev.magadiflo.order.app.dto.response.OrderDetailResponse;
import dev.magadiflo.order.app.dto.response.OrderResponse;
import dev.magadiflo.order.app.entity.Order;
import dev.magadiflo.order.app.entity.OrderDetail;
import dev.magadiflo.order.app.model.Status;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.List;

@UtilityClass
public class OrderMapper {
    public Order toCreateEntityOrder(CreateOrderRequest request, String generatedOrderCode, BigDecimal totalAmount) {
        Order order = Order.builder()
                .orderCode(generatedOrderCode)
                .customerCode(request.customerCode())
                .totalAmount(totalAmount)
                .currency(request.currency())
                .status(Status.PENDING) // Estado inicial
                .build();
        request.items().forEach(itemDto -> {
            OrderDetail orderDetail = toOrderDetail(itemDto);
            order.addOrderDetail(orderDetail);
        });
        return order;
    }

    public OrderDetail toOrderDetail(OrderDetailRequest request) {
        return OrderDetail.builder()
                .productCode(request.productCode())
                .quantity(request.quantity())
                .price(request.price())
                .build();
    }

    public OrderResponse toOrderResponse(Order order) {
        List<OrderDetailResponse> itemsResponse = order.getOrderDetails().stream()
                .map(orderDetail -> new OrderDetailResponse(
                        orderDetail.getProductCode(),
                        orderDetail.getQuantity(),
                        orderDetail.getPrice()
                )).toList();
        return new OrderResponse(
                order.getOrderCode(),
                order.getCustomerCode(),
                order.getTotalAmount(),
                order.getCurrency(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                itemsResponse
        );
    }
}
