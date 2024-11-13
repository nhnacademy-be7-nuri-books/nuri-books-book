package shop.nuribooks.books.cart.service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

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
import shop.nuribooks.books.cart.repository.DBCartRepository;
import shop.nuribooks.books.cart.repository.RedisCartRepository;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisKeyExpirationListener implements MessageListener {
	private static final String SHADOW_KEY = "shadow:";
	private static final String MEMBER_CART_KEY = "member:";

	private final RedisCartRepository redisCartRepository;
	private final DBCartRepository dbCartRepository;
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
			//TODO: 기존에 카트를 가지고 있었으면 날리고 새로 만들어 주어야 한다

			Cart cart = dbCartRepository.findByMember_Id(memberId)
				.orElseGet(() -> createCartForMember(memberId));
			cartDetailRepository.deleteByCart(cart);

			String memberCartId = MEMBER_CART_KEY + parsedKey;
			Map<Long, Integer> cartDetails = redisCartRepository.getCart(memberCartId);

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

	private Cart createCartForMember(Long memberId) {
		// 회원이 존재하지 않을때 어떻게 해야하나?
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

		Cart newCart = new Cart(member);
		return dbCartRepository.save(newCart);
	}
}
