package shop.nuribooks.books.book.book.dto;

public record AladinBookListResponse(
	String title,
	String author,
	String isbn,
	int priceStandard,
	String cover
) {
}
