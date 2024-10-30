package shop.nuribooks.books.cart.customer.service;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.nuribooks.books.cart.customer.dto.request.CustomerCartAddRequest;
import shop.nuribooks.books.cart.customer.dto.response.CustomerCartResponse;
import shop.nuribooks.books.cart.customer.entitiy.CustomerCart;
import shop.nuribooks.books.cart.customer.repository.CustomerCartRepository;

@RequiredArgsConstructor
@Service
public class CustomerCartServiceImpl implements CustomerCartService {
    public static final String CART_KEY = "cart:";
    private final CustomerCartRepository customerCartRepository;

    // 비회원 장바구니 담기
    @Override
    public void addToCart(CustomerCartAddRequest request) {
        CustomerCart customerCart = CustomerCart.builder()
                .id(request.sessionId())
                .bookId(request.bookId().toString())
                .quantity(request.quantity())
                .build();
        customerCartRepository.addCart(customerCart);
    }

    //비회원 장바구니 조회
    @Override
    public List<CustomerCartResponse> getCustomerCartList(String sessionId) {
        Map<String, Integer> cart = customerCartRepository.getCart(sessionId);
        return cart.entrySet().stream()
                .map(tuple -> new CustomerCartResponse(Long.parseLong(tuple.getKey()), tuple.getValue()))
                .toList();
    }

    //비회원 장바구니 전체 삭제
    @Override
    public void removeCustomerCart(String sessionId) {
        customerCartRepository.removeCart(sessionId);
    }

    //비회원 장바구니 특정 도서 삭제




}
