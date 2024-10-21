package shop.nuribooks.books.dto.errordto.response;

public record ErrorResponseDto(int statusCode, String message, String details) {
}
