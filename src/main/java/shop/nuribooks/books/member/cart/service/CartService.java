package shop.nuribooks.books.member.cart.service;

import shop.nuribooks.books.member.cart.dto.CartAddResponse;

/**
 * @author Jprotection
 */
public interface CartService {
	CartAddResponse addToCart(Long memberId, Long bookId);
}
