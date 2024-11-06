package shop.nuribooks.books.cart.customer.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import shop.nuribooks.books.cart.customer.entity.CustomerCart;

public record CustomerCartAddRequest(@NotBlank String sessionId,
                                     @NotNull Long bookId,
                                     @Min(value = 1) int quantity) {

    public CustomerCart toEntity() {
        return CustomerCart.builder()
                .id(sessionId)
                .bookId(bookId.toString())
                .quantity(quantity)
                .build();
    }
}
