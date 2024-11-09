package shop.nuribooks.books.member.cart.service;

import java.util.List;

import shop.nuribooks.books.member.cart.dto.response.CartAddResponse;
import shop.nuribooks.books.member.cart.dto.response.CartListResponse;
import shop.nuribooks.books.member.cart.dto.response.CartUpdateResponse;

/**
 * @author Jprotection
 */
public interface CartService {

	CartAddResponse addToCart(Long memberId, Long bookId, int quantity);

	List<CartListResponse> getCartList(Long memberId);

	void deleteCart(Long memberId, Long bookId);

	CartUpdateResponse updateCart(Long memberId, Long bookId, int quantity);
}
