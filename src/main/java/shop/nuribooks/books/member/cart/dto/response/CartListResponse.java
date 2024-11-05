package shop.nuribooks.books.member.cart.dto.response;

import java.math.BigDecimal;

import lombok.Builder;
import shop.nuribooks.books.book.book.entitiy.BookStateEnum;

@Builder
public record CartListResponse(

	BookStateEnum state,
	String title,
	String thumbnailImageUrl,
	BigDecimal price,
	int discountRate,
	boolean isPackageable
) {}
