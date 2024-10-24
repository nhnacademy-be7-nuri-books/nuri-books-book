package shop.nuribooks.books.dto.bookstate;

import jakarta.validation.constraints.NotNull;
import shop.nuribooks.books.entity.book.BookStateEnum;

public record BookStateRequest(@NotNull(message = "도서상태명은 필수입니다.") String detail) {
}
