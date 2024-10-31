package shop.nuribooks.books.book.book.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.publisher.entitiy.Publisher;

//TODO: Publisher 엔티티를 직접 포함하지않고 String으로 출판사명으로 교체해야될듯
public record AdminBookListResponse(
	Long id,
	Publisher publisher,
	String state,
	String title,
	BigDecimal price,
	int discountRate,
	boolean isPackageable,
	int stock
) {
	public static AdminBookListResponse of(Book book) {
		return new AdminBookListResponse(
			book.getId(),
			book.getPublisherId(),
			book.getState().getKorName(),
			book.getTitle(),
			book.getPrice(),
			book.getDiscountRate(),
			book.isPackageable(),
			book.getStock()
		);
	}
}
