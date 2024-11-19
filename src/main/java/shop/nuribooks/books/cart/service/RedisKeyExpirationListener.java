package shop.nuribooks.books.cart.service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.cart.cartdetail.entity.CartDetail;
import shop.nuribooks.books.cart.cartdetail.repository.CartDetailRepository;
import shop.nuribooks.books.cart.entity.Cart;
import shop.nuribooks.books.cart.repository.CartRepository;
import shop.nuribooks.books.cart.repository.RedisCartRepository;
import shop.nuribooks.books.exception.cart.CartNotFoundException;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisKeyExpirationListener implements MessageListener {
	private static final String SHADOW_KEY = "expire:timer:";
	private static final String MEMBER_CART_KEY = "member:";

	private final RedisCartRepository redisCartRepository;
	private final BookRepository bookRepository;
	private final CartDetailRepository cartDetailRepository;
	private final CartRepository cartRepository;

	@Override
	@Transactional
	public void onMessage(Message message, byte[] pattern) {

		String expiredKey = new String(message.getBody(), StandardCharsets.UTF_8);

		if (expiredKey.startsWith(SHADOW_KEY + MEMBER_CART_KEY)) {
			String parsedMemberId = expiredKey.substring((SHADOW_KEY + MEMBER_CART_KEY).length());
			Long memberId = Long.parseLong(parsedMemberId);
			String memberCartId = MEMBER_CART_KEY + parsedMemberId;
			try {
				Cart cart = cartRepository.findByMember_Id(memberId).orElseThrow(CartNotFoundException::new);
				Map<Long, Integer> cartDetails = redisCartRepository.getCart(memberCartId);
				List<CartDetail> cartDetailList = getCartDetailList(cartDetails, cart);
				cartDetailRepository.deleteByCart(cart);
				cartDetailRepository.saveAll(cartDetailList);
			} catch (Exception e) {
				log.error("에러를 무시합니다.");
			} finally {
				redisCartRepository.removeCart(memberCartId);
			}
		}
	}

	private List<CartDetail> getCartDetailList(Map<Long, Integer> cartDetails, Cart cart) {
		return cartDetails.entrySet().stream()
			.map(cartDetail -> {
					Book book = bookRepository.findById(cartDetail.getKey()).orElseThrow();
					return new CartDetail(cart, book, cartDetail.getValue());
				}
			)
			.toList();
	}
}
