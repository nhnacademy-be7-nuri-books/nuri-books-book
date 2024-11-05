package shop.nuribooks.books.member.cart.service;

import java.util.List;

import shop.nuribooks.books.member.cart.dto.request.CartAddRequest;
import shop.nuribooks.books.member.cart.dto.response.CartAddResponse;
import shop.nuribooks.books.member.cart.dto.response.CartListResponse;

/**
 * @author Jprotection
 */
public interface CartService {

	CartAddResponse addToCart(CartAddRequest request);

	List<CartListResponse> getCartList(Long memberId);
}
