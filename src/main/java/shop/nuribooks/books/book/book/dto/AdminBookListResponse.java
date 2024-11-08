package shop.nuribooks.books.book.book.dto;

import java.math.BigDecimal;
import java.util.List;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;

//TODO: 관리자 전용 dto라 생각했는데 회원과 공통으로 사용해도 될듯하여 이름변경 생각해보자
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
	public static AdminBookListResponse of(Book book, BigDecimal salePrice) {
		return new AdminBookListResponse(
			book.getId(),
			book.getPublisherId().getName(),
			book.getState().getKorName(),
			book.getTitle(),
			book.getPrice(),
			salePrice,
			book.getDiscountRate(),
			book.isPackageable(),
			book.getStock(),
			book.getThumbnailImageUrl()
		);
	}
}
