package shop.nuribooks.books.cart.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.cart.cartdetail.entity.CartDetail;
import shop.nuribooks.books.cart.cartdetail.entity.RedisCartDetail;
import shop.nuribooks.books.cart.cartdetail.repository.CartDetailRepository;
import shop.nuribooks.books.cart.dto.request.CartAddRequest;
import shop.nuribooks.books.cart.dto.request.CartLoadRequest;
import shop.nuribooks.books.cart.dto.response.CartBookResponse;
import shop.nuribooks.books.cart.entity.Cart;
import shop.nuribooks.books.cart.repository.CartRepository;
import shop.nuribooks.books.cart.repository.RedisCartRepository;
import shop.nuribooks.books.exception.cart.CartNotFoundException;

@ExtendWith(MockitoExtension.class)
class CustomerCartServiceImplUnitTest {

	@InjectMocks
	private CartServiceImpl cartService;

	@Mock
	private RedisCartRepository redisCartRepository;

	@Mock
	private CartRepository cartRepository;

	@Mock
	private CartDetailRepository cartDetailRepository;

	@Mock
	private BookRepository bookRepository;

	@DisplayName("비회원 장바구니에 요청받은 책과 수량을 저장한다.")
	@Test
	void addCustomerCart() {
		// given
		String customerCartId = "customerCartId";
		CartAddRequest customerCartAddRequest = new CartAddRequest(customerCartId, 1L, 1);

		// when
		cartService.addCustomerCart(customerCartAddRequest);

		// then
		verify(redisCartRepository).addCart(anyString(), any(RedisCartDetail.class));
		verify(redisCartRepository).setExpire(anyString(), anyInt(), any(TimeUnit.class));
	}

	@DisplayName("회원 장바구니에 요청받은 책과 수량을 저장한다.")
	@Test
	void addMemberCart() {
		// given
		String memberCartId = "memberCartId";
		CartAddRequest memberCartAddRequest = new CartAddRequest(memberCartId, 1L, 1);

		// when
		cartService.addMemberCart(memberCartAddRequest);

		// then
		verify(redisCartRepository).addCart(anyString(), any(RedisCartDetail.class));
		verify(redisCartRepository).setShadowExpireKey(anyString(), anyInt(), any(TimeUnit.class));
	}

	@DisplayName("cartId에 대한 장바구니를 가져온다.")
	@Test
	void getCustomerCartList() {
		// given
		String cartId = "cartId";
		when(redisCartRepository.getCart(cartId)).thenReturn(Map.of(
			1L, 3,
			2L, 4
		));

		Book book = mock(Book.class);
		Optional<Book> optionalBook = Optional.of(book);
		when(book.getPrice()).thenReturn(BigDecimal.valueOf(100));
		when(book.getDiscountRate()).thenReturn(10);
		when(bookRepository.findById(anyLong())).thenReturn(optionalBook);

		// when
		List<CartBookResponse> responseList = cartService.getCart(cartId);

		// then
		assertThat(responseList).hasSize(2);

	}

	@DisplayName("cartId에 대한 장바구니를 가져온다.")
	@Test
	void getMemberCartList() {
		// given
		String cartId = "member:";
		when(redisCartRepository.getCart(cartId)).thenReturn(Map.of(
			1L, 3,
			2L, 4
		));

		Book book = mock(Book.class);
		Optional<Book> optionalBook = Optional.of(book);
		when(book.getPrice()).thenReturn(BigDecimal.valueOf(100));
		when(book.getDiscountRate()).thenReturn(10);
		when(bookRepository.findById(anyLong())).thenReturn(optionalBook);

		// when
		List<CartBookResponse> responseList = cartService.getCart(cartId);

		// then
		assertThat(responseList).hasSize(2);

	}

	@DisplayName("장바구니를 삭제한다.")
	@Test
	void removeCustomerCart() {
		// given
		String cartId = "cartId";

		// when
		cartService.removeCart(cartId);

		// then
		verify(redisCartRepository).removeCart(anyString());
	}

	@DisplayName("장바구니에서 특정 아이템을 삭제한다.")
	@Test
	void removeCustomerCartItem() {
		// given
		String cartId = "cartId";

		// when
		cartService.removeCartItem(cartId, 1L);

		// then
		verify(redisCartRepository).removeCartItem(anyString(), anyString());
	}

	@DisplayName("회원 장바구니를 redis에 올린다.")
	@Test
	void loadCart() {
		// given
		Cart cart = mock(Cart.class);
		Optional<Cart> optionalCart = Optional.of(cart);
		when(cartRepository.findByMember_Id(anyLong())).thenReturn(optionalCart);
		when(redisCartRepository.isExist(anyString())).thenReturn(false);
		CartLoadRequest request = new CartLoadRequest(1L);

		CartDetail cartDetail = mock(CartDetail.class);
		Optional<List<CartDetail>> optionalCartDetails = Optional.of(List.of(cartDetail));
		when(cartDetailRepository.findAllByCart_Id(anyLong())).thenReturn(optionalCartDetails);
		Book book = mock(Book.class);
		when(cartDetail.getBook()).thenReturn(book);
		when(book.getId()).thenReturn(1L);

		// when
		cartService.loadCart(request);

		// then
		verify(redisCartRepository).saveAll(anyString(), anyList());
		verify(redisCartRepository).setShadowExpireKey(anyString(), anyInt(), any(TimeUnit.class));

	}

	@DisplayName("회원 장바구니를 redis에 올린다.")
	@Test
	void loadCart_cartNull() {
		// given
		when(cartRepository.findByMember_Id(anyLong())).thenReturn(Optional.empty());
		CartLoadRequest request = new CartLoadRequest(1L);

		// then
		Assertions.assertThatThrownBy(() -> cartService.loadCart(request)).isInstanceOf(CartNotFoundException.class);
	}

	@DisplayName("회원 장바구니를 redis에 올린다.")
	@Test
	void loadCart_redisCartExist() {
		// given
		Cart cart = mock(Cart.class);
		Optional<Cart> optionalCart = Optional.of(cart);
		when(cartRepository.findByMember_Id(anyLong())).thenReturn(optionalCart);
		when(redisCartRepository.isExist(anyString())).thenReturn(true);
		CartLoadRequest request = new CartLoadRequest(1L);

		// when
		cartService.loadCart(request);

		// then
		verify(redisCartRepository).isExist(anyString());
	}

	@DisplayName("회원 장바구니를 redis에 올린다.")
	@Test
	void loadCart_allEmpty() {
		// given
		Cart cart = mock(Cart.class);
		Optional<Cart> optionalCart = Optional.of(cart);
		when(cartRepository.findByMember_Id(anyLong())).thenReturn(optionalCart);
		when(redisCartRepository.isExist(anyString())).thenReturn(false);
		CartLoadRequest request = new CartLoadRequest(1L);

		when(cartDetailRepository.findAllByCart_Id(anyLong())).thenReturn(Optional.empty());

		// when
		cartService.loadCart(request);

		// then
		verify(redisCartRepository).isExist(anyString());
	}

}
