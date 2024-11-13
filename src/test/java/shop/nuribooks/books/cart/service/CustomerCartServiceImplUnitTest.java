package shop.nuribooks.books.cart.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.service.BookService;
import shop.nuribooks.books.cart.dto.request.CartAddRequest;
import shop.nuribooks.books.cart.dto.response.CartResponse;
import shop.nuribooks.books.cart.entity.Cart;
import shop.nuribooks.books.cart.repository.RedisCartRepository;

@ExtendWith(MockitoExtension.class)
class CustomerCartServiceImplUnitTest {

//    @InjectMocks
//    private CartServiceImpl customerCartService;
//
//    @Mock
//    private RedisCartRepository customerCartRepository;
//
//    @Mock
//    private BookService bookService;
//
//    @DisplayName("요청받은 책과 수량을 repository에 저장한다.")
//    @Test
//    void addToCart() {
//        // given
//        String sessionId = "sessionId";
//        CartAddRequest customerCartAddRequest = new CartAddRequest(1L, 1);
//
//        // when
//        customerCartService.addToCart(sessionId, customerCartAddRequest);
//
//        // then
//        verify(customerCartRepository, times(1)).addCart(anyString(), any(Cart.class));
//    }
//
//    @DisplayName("sessionId에 대한 장바구니를 가져온다.")
//    @Test
//    void getCustomerCartList() {
//        // given
//        String sessionId = "sessionId";
//        when(customerCartRepository.getCart(sessionId)).thenReturn(Map.of(
//                "1", 3,
//                "2", 4
//        ));
//
//
//        when(bookService.getBookById(anyLong())).thenReturn(any(BookResponse.class));
//
//        // when
//        List<CartResponse> customerCartList = customerCartService.getCustomerCartList(sessionId);
//
//        // then
//        assertThat(customerCartList).hasSize(2);
//
//    }
//
//    @DisplayName("장바구니를 삭제한다.")
//    @Test
//    void removeCustomerCart() {
//        // given
//        String sessionId = "sessionId";
//
//        // when
//        customerCartService.removeCart(sessionId);
//
//        // then
//        verify(customerCartRepository, times(1)).removeCart(anyString());
//    }
//
//    @DisplayName("장바구니에서 특정 아이템을 삭제한다.")
//    @Test
//    void removeCustomerCartItem() {
//        // given
//        String sessionId = "sessionId";
//
//        // when
//        customerCartService.removeCartItem(sessionId, 1L);
//
//        // then
//        verify(customerCartRepository, times(1)).removeCartItem(anyString(), anyString());
//    }

}