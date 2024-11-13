package shop.nuribooks.books.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import shop.nuribooks.books.cart.entity.Cart;

public record CartAddRequest(
        @NotBlank String cartId,
        @NotNull Long bookId,
        @Min(value = 1) int quantity) {
}
