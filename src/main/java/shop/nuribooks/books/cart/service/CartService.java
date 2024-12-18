package shop.nuribooks.books.cart.service;

import java.util.List;

import shop.nuribooks.books.cart.dto.request.CartAddRequest;
import shop.nuribooks.books.cart.dto.request.CartLoadRequest;
import shop.nuribooks.books.cart.dto.response.CartBookResponse;

public interface CartService {

	void addCustomerCart(CartAddRequest request);

	void addMemberCart(CartAddRequest request);

	//비회원 장바구니 조회
	List<CartBookResponse> getCart(String cartId);

	//비회원 장바구니 삭제
	void removeCart(String sessionId);

	//비회원 장바구니 특정 도서 삭제
	void removeCartItem(String cartId, Long bookId);

	void loadCart(CartLoadRequest request);
}
