package shop.nuribooks.books.cart.dto.response;

import shop.nuribooks.books.book.book.dto.BookResponse;

public record CartResponse(CartBookResponse cartBookResponse, int quantity) {
}
