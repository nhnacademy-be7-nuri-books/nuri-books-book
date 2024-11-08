package shop.nuribooks.books.member.cart.dto.response;

import java.math.BigDecimal;

import lombok.Builder;
import shop.nuribooks.books.book.book.entity.BookStateEnum;


@Builder
public record CartAddResponse (

	BookStateEnum state,
	String title,
	String thumbnailImageUrl,
	BigDecimal price,
	int discountRate,
	boolean isPackageable
) {}
