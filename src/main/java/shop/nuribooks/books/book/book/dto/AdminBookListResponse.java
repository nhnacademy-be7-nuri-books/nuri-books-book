package shop.nuribooks.books.book.book.dto;

import java.math.BigDecimal;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.utility.BookUtils;

public record AdminBookListResponse(
	Long id,
	String publisherName,
	String state,
	String title,
	BigDecimal price,
	BigDecimal salePrice,
	int discountRate,
	boolean isPackageable,
	int stock,
	String thumbnailImageUrl
) {
	public static AdminBookListResponse of(Book book) {
		return new AdminBookListResponse(
			book.getId(),
			book.getPublisherId().getName(),
			book.getState().getKorName(),
			book.getTitle(),
			book.getPrice(),
			BookUtils.calculateSalePrice(book.getPrice(), book.getDiscountRate()),
			book.getDiscountRate(),
			book.isPackageable(),
			book.getStock(),
			book.getThumbnailImageUrl()
		);
	}
}
