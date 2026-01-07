package dev.magadiflo.order.app.dto.response;

import dev.magadiflo.order.app.model.Currency;
import dev.magadiflo.order.app.model.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(String orderCode,
                            String customerCode,
                            BigDecimal totalAmount,
                            Currency currency,
                            Status status,
                            LocalDateTime createdAt,
                            LocalDateTime updatedAt,
                            List<OrderDetailResponse> items) {
}
