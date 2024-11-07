package shop.nuribooks.books.member.cart.dto;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.member.cart.dto.response.CartAddResponse;
import shop.nuribooks.books.member.cart.dto.response.CartListResponse;
import shop.nuribooks.books.member.cart.entity.Cart;

public class DtoMapper {

	public static CartAddResponse toCartAddDto(Cart cart) {
		Book book = cart.getBook();

		return CartAddResponse.builder()
			.state(book.getState())
			.title(book.getTitle())
			.thumbnailImageUrl(book.getThumbnailImageUrl())
			.price(book.getPrice())
			.discountRate(book.getDiscountRate())
			.isPackageable(book.isPackageable())
			.build();
	}

	public static CartAddResponse toCartNullDto() {
		return CartAddResponse.builder().build();
	}

	public static CartListResponse toCartListDto(Cart cart) {
		Book book = cart.getBook();

		return CartListResponse.builder()
			.state(book.getState())
			.title(book.getTitle())
			.thumbnailImageUrl(book.getThumbnailImageUrl())
			.price(book.getPrice())
			.discountRate(book.getDiscountRate())
			.isPackageable(book.isPackageable())
			.build();
	}

	public static CartListResponse toCartListNullDto() {
		return CartListResponse.builder().build();
	}
}