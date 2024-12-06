package shop.nuribooks.books.book.book.dto.response;

import java.math.BigDecimal;
import java.util.List;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.utility.BookUtils;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;

public record BookOrderResponse(
	Long bookId,
	String title,
	String thumbnailImageUrl,
	BigDecimal price,
	int discountRate,
	BigDecimal salePrice,
	boolean isPackageable,
	int stock,
	List<BookContributorInfoResponse> contributors,
	int quantity,
	BigDecimal bookTotalPrice
) {
	public static BookOrderResponse of(Book book, List<BookContributorInfoResponse> contributors, int quantity) {

		BigDecimal salePrice = BookUtils.calculateSalePrice(book.getPrice(), book.getDiscountRate());

		return new BookOrderResponse(
			book.getId(),
			book.getTitle(),
			book.getThumbnailImageUrl(),
			book.getPrice(),
			book.getDiscountRate(),
			salePrice,
			book.isPackageable(),
			book.getStock(),
			contributors,
			quantity,
			BookUtils.calculateTotalPrice(salePrice, quantity)
		);
	}

}
