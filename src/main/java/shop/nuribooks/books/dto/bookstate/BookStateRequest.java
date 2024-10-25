package shop.nuribooks.books.dto.bookstate;

import jakarta.validation.constraints.NotBlank;

public record BookStateRequest(@NotBlank(message = "도서상태명은 필수입니다.") String detail) {
}
