package shop.nuribooks.books.cart.service;

import java.util.List;
import shop.nuribooks.books.cart.dto.request.CartAddRequest;
import shop.nuribooks.books.cart.dto.response.CartResponse;

public interface CartService {

    void addCustomerCart(String cartId, CartAddRequest request);

    void addMemberCart(String cartId, CartAddRequest request);

    //비회원 장바구니 조회
    List<CartResponse> getCart(String cartId);

    //비회원 장바구니 삭제
    void removeCart(String sessionId);

    //비회원 장바구니 특정 도서 삭제
    void removeCartItem(String sessionId, Long bookId);

}
