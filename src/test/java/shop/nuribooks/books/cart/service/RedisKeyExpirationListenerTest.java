package shop.nuribooks.books.cart.service;

import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.StringRedisTemplate;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.cart.cartdetail.repository.CartDetailRepository;
import shop.nuribooks.books.cart.entity.Cart;
import shop.nuribooks.books.cart.entity.RedisCartKey;
import shop.nuribooks.books.cart.repository.CartRepository;
import shop.nuribooks.books.cart.repository.RedisCartRepository;
import shop.nuribooks.books.exception.cart.CartNotFoundException;
import shop.nuribooks.books.member.member.entity.Member;

class RedisKeyExpirationListenerTest {

	@InjectMocks
	private RedisKeyExpirationListener redisKeyExpirationListener;

	@Mock
	private RedisCartRepository redisCartRepository;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private CartDetailRepository cartDetailRepository;

	@Mock
	private CartRepository cartRepository;

	@Mock
	private StringRedisTemplate stringRedisTemplate;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void onMessage_whenKeyIsExpiredAndCartExists_savesCartDetails() {
		// Arrange
		Long memberId = 1L;
		String memberCartId = RedisCartKey.MEMBER_CART.withSuffix(String.valueOf(memberId));
		String expiredKey = RedisCartKey.SHADOW_KEY.getKey() + memberCartId;

		Message message = getMessage(expiredKey);
		Member member = new Member();
		Map<Long, Integer> cartDetails = Map.of(1001L, 2, 1002L, 3);

		Cart cart = new Cart(member);
		Book book1 = Book.builder().build();
		Book book2 = Book.builder().build();

		when(cartRepository.findByMember_Id(memberId)).thenReturn(Optional.of(cart));
		when(redisCartRepository.getCart(memberCartId)).thenReturn(cartDetails);
		when(bookRepository.findById(1001L)).thenReturn(Optional.of(book1));
		when(bookRepository.findById(1002L)).thenReturn(Optional.of(book2));

		// Act
		redisKeyExpirationListener.onMessage(message, null);

		// Assert
		verify(cartDetailRepository).deleteByCart(cart);
		verify(cartDetailRepository).saveAll(anyList());
		verify(redisCartRepository).removeCart(memberCartId);
	}

	@Test
	void onMessage_whenCartNotFound_logsErrorAndContinues() {
		// Arrange
		Long memberId = 1L;
		String memberCartId = RedisCartKey.MEMBER_CART.withSuffix(String.valueOf(memberId));
		String expiredKey = RedisCartKey.SHADOW_KEY.getKey() + memberCartId;

		Message message = getMessage(expiredKey);

		when(cartRepository.findByMember_Id(memberId)).thenThrow(new CartNotFoundException());

		// Act
		redisKeyExpirationListener.onMessage(message, null);

		// Assert
		verify(cartDetailRepository, never()).deleteByCart(any());
		verify(cartDetailRepository, never()).saveAll(anyList());
		verify(redisCartRepository).removeCart(memberCartId);
	}


	@Test
	void onMessage_timerKeyWrong() {
		// Arrange
		Long memberId = 1L;
		String memberCartId = RedisCartKey.MEMBER_CART.withSuffix(String.valueOf(memberId));
		String expiredKey = "hi:)";

		Message message = getMessage(expiredKey);

		// Act
		redisKeyExpirationListener.onMessage(message, null);

		// Assert
		verify(cartDetailRepository, never()).deleteByCart(any());
		verify(cartDetailRepository, never()).saveAll(anyList());
		verify(redisCartRepository, never()).removeCart(memberCartId);
	}

	@Test
	void onMessage_whenBookNotFound_throwsExceptionAndRemovesCart() {
		// Arrange
		Long memberId = 1L;
		String memberCartId = RedisCartKey.MEMBER_CART.withSuffix(String.valueOf(memberId));
		String expiredKey = RedisCartKey.SHADOW_KEY.getKey() + memberCartId;

		Message message = getMessage(expiredKey);
		Member member = new Member();
		Map<Long, Integer> cartDetails = Map.of(1001L, 2);

		Cart cart = new Cart(member);

		when(cartRepository.findByMember_Id(memberId)).thenReturn(Optional.of(cart));
		when(redisCartRepository.getCart(memberCartId)).thenReturn(cartDetails);
		when(bookRepository.findById(1001L)).thenReturn(Optional.empty());

		// Act & Assert
		redisKeyExpirationListener.onMessage(message, null);

		verify(redisCartRepository).removeCart(memberCartId);
		verify(cartDetailRepository, never()).saveAll(any());
	}

	private Message getMessage(String expiredKey){
		return new Message() {
			@Override
			public byte[] getBody() {
				return expiredKey.getBytes(StandardCharsets.UTF_8);
			}

			@Override
			public byte[] getChannel() {
				return new byte[0];
			}
		};

	}
}