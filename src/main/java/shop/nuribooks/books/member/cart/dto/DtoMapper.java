package shop.nuribooks.books.member.cart.dto;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.member.cart.dto.response.CartAddResponse;
import shop.nuribooks.books.member.cart.dto.response.CartListResponse;
import shop.nuribooks.books.member.cart.dto.response.CartUpdateResponse;
import shop.nuribooks.books.member.cart.entity.Cart;

public class DtoMapper {

	public static CartAddResponse toCartAddDto(Cart cart) {
		Book book = cart.getBook();

		return CartAddResponse.builder()
			.bookId(book.getId())
			.state(book.getState())
			.title(book.getTitle())
			.thumbnailImageUrl(book.getThumbnailImageUrl())
			.price(book.getPrice())
			.discountRate(book.getDiscountRate())
			.isPackageable(book.isPackageable())
			.quantity(cart.getQuantity())
			.build();
	}

	public static CartListResponse toCartListDto(Cart cart) {
		Book book = cart.getBook();

		return CartListResponse.builder()
			.bookId(book.getId())
			.state(book.getState())
			.title(book.getTitle())
			.thumbnailImageUrl(book.getThumbnailImageUrl())
			.price(book.getPrice())
			.discountRate(book.getDiscountRate())
			.isPackageable(book.isPackageable())
			.quantity(cart.getQuantity())
			.build();
	}

	public static CartUpdateResponse toCartUpdateDto(Cart cart) {
		Book book = cart.getBook();

		return CartUpdateResponse.builder()
			.bookId(book.getId())
			.state(book.getState())
			.title(book.getTitle())
			.thumbnailImageUrl(book.getThumbnailImageUrl())
			.price(book.getPrice())
			.discountRate(book.getDiscountRate())
			.isPackageable(book.isPackageable())
			.quantity(cart.getQuantity())
			.build();
	}
}
