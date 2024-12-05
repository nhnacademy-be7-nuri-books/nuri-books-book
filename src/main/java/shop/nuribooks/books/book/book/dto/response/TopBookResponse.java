package shop.nuribooks.books.book.book.dto.response;

public record TopBookResponse(
	Long bookId,
	String thumbnailUrl,
	String title
) {
}
