package shop.nuribooks.books.book.book.dto;

public record TopBookResponse(
	Long bookId,
	String thumbnailUrl,
	String title
) {
}
