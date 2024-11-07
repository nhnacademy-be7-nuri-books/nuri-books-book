package shop.nuribooks.books.book.book.dto;

public record AladinBookResponse(
	String title,
	String author,
	String pubDate,
	String isbn,
	String publisher,
	int priceSales,
	int priceStandard,
	String cover,
	String description,
	int discountRate,
	String category
) {
}
