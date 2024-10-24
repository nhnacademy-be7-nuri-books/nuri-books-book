package shop.nuribooks.books.dto.bookstate;

import shop.nuribooks.books.entity.book.BookStateEnum;

public record BookStateResponse(Integer id, BookStateEnum detail) {
}
