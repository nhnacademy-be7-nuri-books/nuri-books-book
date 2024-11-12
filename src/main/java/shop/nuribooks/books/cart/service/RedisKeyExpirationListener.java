package shop.nuribooks.books.cart.service;

import static java.util.stream.Collectors.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.book.service.BookService;
import shop.nuribooks.books.cart.cartdetail.entity.CartDetail;
import shop.nuribooks.books.cart.cartdetail.entity.CartDetailId;
import shop.nuribooks.books.cart.cartdetail.repository.CartDetailRepository;
import shop.nuribooks.books.cart.entity.Cart;
import shop.nuribooks.books.cart.repository.DBCartRepository;
import shop.nuribooks.books.cart.repository.RedisCartRepository;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;
import shop.nuribooks.books.member.member.service.MemberService;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisKeyExpirationListener implements MessageListener {
	private static final String MEMBER_CART_KEY = "member:";

	private final RedisCartRepository redisCartRepository;
	private final DBCartRepository dbCartRepository;
	private final BookRepository bookRepository;
	private final CartDetailRepository cartDetailRepository;
	private final MemberRepository memberRepository;

	@Override
	public void onMessage(Message message, byte[] pattern) {
		String expiredKey = new String(message.getBody(), StandardCharsets.UTF_8);
		// String expiredKeyToString = message.toString();

		if (expiredKey.startsWith("member:")) {
			Long memberId = Long.parseLong(getMemberId(expiredKey));

			//TODO: 기존에 카트를 가지고 있었으면 날리고 새로 만들어 주어야 한다
			Cart cart = dbCartRepository.findByMember_Id(memberId).orElseGet(
				() -> createCartForMember(memberId));

			Map<String, Integer> cartDetails = getDataFromRedis(expiredKey);

			List<CartDetail> cartDetailList = cartDetails.entrySet().stream()
				.map(cartDetail -> {
						CartDetailId cartDetailId = new CartDetailId(cart.getId(), Long.parseLong(cartDetail.getKey()));
						Book book = bookRepository.findById(Long.parseLong(cartDetail.getKey())).orElseThrow();
						return new CartDetail(cartDetailId, cart, book, cartDetail.getValue());
					}
				)
				.toList();
			cartDetailRepository.saveAll(cartDetailList);
		}
	}

	private Map<String, Integer> getDataFromRedis(String key) {
		return redisCartRepository.getCart(key);
	}

	private String getMemberId(String expiredKey) {
		return "uuid";
	}

	private Cart createCartForMember(Long memberId) {
		// 회원이 존재하지 않을때 어떻게 해야하나?
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

		Cart newCart = Cart.builder()
			.id(UUID.randomUUID().toString())
			.member(member)
			.build();
		return dbCartRepository.save(newCart);
	}
}
