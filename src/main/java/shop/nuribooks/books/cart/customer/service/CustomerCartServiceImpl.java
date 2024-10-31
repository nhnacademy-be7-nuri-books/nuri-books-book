package shop.nuribooks.books.cart.customer.service;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.nuribooks.books.book.book.service.BookService;
import shop.nuribooks.books.cart.customer.dto.request.CustomerCartAddRequest;
import shop.nuribooks.books.cart.customer.dto.response.CustomerCartResponse;
import shop.nuribooks.books.cart.customer.entitiy.CustomerCart;
import shop.nuribooks.books.cart.customer.repository.CustomerCartRepository;

@RequiredArgsConstructor
@Service
public class CustomerCartServiceImpl implements CustomerCartService {
    public static final String CART_KEY = "cart:";

    private final CustomerCartRepository customerCartRepository;
    private final BookService bookService;

    // 비회원 장바구니 담기
    @Override
    public void addToCart(CustomerCartAddRequest request) {
        CustomerCart customerCart = request.toEntity();

        customerCartRepository.addCart(customerCart);
    }

    //비회원 장바구니 조회
    @Override
    public List<CustomerCartResponse> getCustomerCartList(String sessionId) {
        // TODO: 세션이 없는경우 예외처리
        Map<String, Integer> cart = customerCartRepository.getCart(sessionId);

        return cart.entrySet().stream()
                .map(cartItem ->
                    new CustomerCartResponse(
                            bookService.getBookById(Long.parseLong(cartItem.getKey())),
                            cartItem.getValue()
                    )
                )
                .toList();
    }

    //비회원 장바구니 전체 삭제
    @Override
    public void removeCustomerCart(String sessionId) {
        //TODO: 장바구니가 없는 경우 예외 처리
        customerCartRepository.removeCart(sessionId);
    }

    //비회원 장바구니 특정 도서 삭제
    @Override
    public void removeCustomerCartItem(String sessionId, Long bookId) {
        //TODO: 아이템 없는 경우 예외 처리
        customerCartRepository.removeCartItem(sessionId, bookId.toString());
    }






}
