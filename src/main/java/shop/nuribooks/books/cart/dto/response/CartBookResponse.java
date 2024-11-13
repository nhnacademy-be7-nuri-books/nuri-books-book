package shop.nuribooks.books.cart.dto.response;

import java.math.BigDecimal;

import lombok.Builder;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.utility.BookUtils;

@Builder
public record CartBookResponse(
	Long bookId,
	String title,
	BigDecimal price,
	int discountRate,
	BigDecimal salePrice,
	BigDecimal totalPrice,
	String thumbnailImageUrl,
	int quantity) {

	public static CartBookResponse of(Book book, int quantity) {
		BigDecimal salePrice = BookUtils.calculateSalePrice(book.getPrice(), book.getDiscountRate());
		BigDecimal totalPrice = BookUtils.calculateTotalPrice(salePrice, quantity);

		return CartBookResponse.builder()
			.bookId(book.getId())
			.title(book.getTitle())
			.price(book.getPrice())
			.discountRate(book.getDiscountRate())
			.salePrice(salePrice)
			.totalPrice(totalPrice)
			.thumbnailImageUrl(book.getThumbnailImageUrl())
			.quantity(quantity)
			.build();
	}

}
