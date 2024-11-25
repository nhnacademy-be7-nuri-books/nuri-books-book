package shop.nuribooks.books.book.book.dto;

public record TopBookLikeResponse(
	Long bookId,
	String thumbnailUrl,
	String title
) {
}
