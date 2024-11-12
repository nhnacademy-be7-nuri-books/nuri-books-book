package shop.nuribooks.books.cart.dto.response;

import java.math.BigDecimal;

import lombok.Builder;
import shop.nuribooks.books.book.book.entity.Book;

@Builder
public record CartBookResponse(
	String title,
	BigDecimal price,
	int discountRate,
	String thumbnailImageUrl) {

	public static CartBookResponse of(Book book) {
		return CartBookResponse.builder()
			.title(book.getTitle())
			.price(book.getPrice())
			.discountRate(book.getDiscountRate())
			.thumbnailImageUrl(book.getThumbnailImageUrl())
			.build();
	}

}
