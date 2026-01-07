package dev.magadiflo.order.app.dto.request;

import dev.magadiflo.order.app.model.Currency;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(@NotBlank
                                 String customerCode,

                                 @NotNull
                                 Currency currency,

                                 @NotEmpty
                                 @Valid // Importante: Valida cada elemento de la lista
                                 List<OrderDetailRequest> items) {
}
