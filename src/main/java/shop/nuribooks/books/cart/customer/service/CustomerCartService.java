package shop.nuribooks.books.cart.customer.service;

import java.util.List;
import shop.nuribooks.books.cart.customer.dto.request.CustomerCartAddRequest;
import shop.nuribooks.books.cart.customer.dto.response.CustomerCartResponse;

public interface CustomerCartService {

    void addToCart(String sessionId, CustomerCartAddRequest request);

    //비회원 장바구니 조회
    List<CustomerCartResponse> getCustomerCartList(String sessionId);

    //비회원 장바구니 삭제
    void removeCustomerCart(String sessionId);

    //비회원 장바구니 특정 도서 삭제
    void removeCustomerCartItem(String sessionId, Long bookId);
}
