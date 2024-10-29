package shop.nuribooks.books.cart.customer.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CustomerCartAddRequest(@NotBlank String sessionId,
                                     @NotNull Long bookId,
                                     @Min(value = 1) int quantity) {
}
