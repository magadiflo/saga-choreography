package dev.magadiflo.order.app.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record OrderDetailResponse(String productCode,
                                  Integer quantity,
                                  BigDecimal price) {
    @JsonProperty
    public BigDecimal subtotal() {
        return this.price.multiply(BigDecimal.valueOf(this.quantity));
    }
}
