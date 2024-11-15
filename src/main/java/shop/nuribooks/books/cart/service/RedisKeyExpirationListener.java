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
	private static final String SHADOW_KEY = "shadow:";
	private static final String MEMBER_CART_KEY = "member:";

	private final RedisCartRepository redisCartRepository;
	private final BookRepository bookRepository;
	private final CartDetailRepository cartDetailRepository;
	private final MemberRepository memberRepository;

	@Override
	@Transactional
	public void onMessage(Message message, byte[] pattern) {
		String expiredKey = new String(message.getBody(), StandardCharsets.UTF_8);

		if (expiredKey.startsWith(SHADOW_KEY + MEMBER_CART_KEY)) {
			String parsedKey = expiredKey.substring((SHADOW_KEY + MEMBER_CART_KEY).length());
			Long memberId = Long.parseLong(parsedKey);
			Cart cart;
			try {
				 cart = getMemberCart(memberId);
			} catch (IllegalArgumentException e) {
				return;
			}

			if (Objects.isNull(cart)) {
				return;
			}
			String memberCartId = MEMBER_CART_KEY + parsedKey;
			Map<Long, Integer> cartDetails = redisCartRepository.getCart(memberCartId);
			if (Objects.isNull(cartDetails)) {
				return;
			}

			List<CartDetail> cartDetailList = getCartDetailList(cartDetails, cart);
			cartDetailRepository.saveAll(cartDetailList);
			redisCartRepository.removeCart(memberCartId);
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

	private Cart getMemberCart(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
		return member.getCart();
	}
}
