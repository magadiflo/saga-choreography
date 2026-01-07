package dev.magadiflo.order.app.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderDetailRequest(@NotBlank
                                 String productCode,

                                 @NotNull
                                 @Min(value = 1)
                                 Integer quantity,

                                 @NotNull
                                 @Positive
                                 BigDecimal price) {
    public BigDecimal subtotal() {
        return this.price.multiply(BigDecimal.valueOf(this.quantity));
    }
}
