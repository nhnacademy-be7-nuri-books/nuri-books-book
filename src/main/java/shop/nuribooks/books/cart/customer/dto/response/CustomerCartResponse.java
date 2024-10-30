package shop.nuribooks.books.cart.customer.dto.response;

import shop.nuribooks.books.book.book.dto.BookResponse;

public record CustomerCartResponse(Long bookId, int quantity) {
}
