package shop.nuribooks.books.member.cart.dto;

import java.math.BigDecimal;

import lombok.Builder;
import shop.nuribooks.books.book.book.entitiy.BookStateEnum;

@Builder
public class CartAddResponse {

	private BookStateEnum state;
	private String title;
	private String thumbnailImageUrl;
	private BigDecimal price;
	private int discountRate;
	private boolean isPackageable;
}
