package shop.nuribooks.books.cart.service;

import static shop.nuribooks.books.cart.entity.RedisCartKey.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.cart.cartdetail.entity.CartDetail;
import shop.nuribooks.books.cart.cartdetail.entity.RedisCartDetail;
import shop.nuribooks.books.cart.cartdetail.repository.CartDetailRepository;
import shop.nuribooks.books.cart.dto.request.CartAddRequest;
import shop.nuribooks.books.cart.dto.request.CartLoadRequest;
import shop.nuribooks.books.cart.dto.response.CartBookResponse;
import shop.nuribooks.books.cart.entity.Cart;
import shop.nuribooks.books.cart.entity.RedisCartKey;
import shop.nuribooks.books.cart.repository.CartRepository;
import shop.nuribooks.books.cart.repository.RedisCartRepository;
import shop.nuribooks.books.exception.cart.CartNotFoundException;

@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService {
	private final BookRepository bookRepository;
	private final RedisCartRepository redisCartRepository;
	private final CartRepository cartRepository;
	private final CartDetailRepository cartDetailRepository;

	@Override
	public void addCustomerCart(CartAddRequest request) {
		String cartId = CUSTOMER_KEY.withSuffix(request.cartId());
		RedisCartDetail redisCartDetail = new RedisCartDetail(request.bookId().toString(), request.quantity());
		redisCartRepository.addCart(cartId, redisCartDetail);
		redisCartRepository.setExpire(cartId, 60, TimeUnit.MINUTES);
	}

	@Override
	public void addMemberCart(CartAddRequest request) {
		String cartId = MEMBER_CART.withSuffix(request.cartId());
		RedisCartDetail redisCartDetail = new RedisCartDetail(request.bookId().toString(), request.quantity());
		redisCartRepository.addCart(cartId, redisCartDetail);
		redisCartRepository.setShadowExpireKey(RedisCartKey.SHADOW_KEY.withSuffix(cartId), 60, TimeUnit.MINUTES);
	}

	@Override
	public List<CartBookResponse> getCart(String cartId) {
		Map<Long, Integer> cart = redisCartRepository.getCart(cartId);
		refreshExpireKey(cartId);
		return convertRedisCartList(cart);
	}

	@Override
	public void removeCart(String cartId) {
		redisCartRepository.removeCart(cartId);
	}

	//비회원 장바구니 특정 도서 삭제
	@Override
	public void removeCartItem(String cartId, Long bookId) {
		redisCartRepository.removeCartItem(cartId, bookId.toString());
	}

	@Override
	public void loadCart(CartLoadRequest request) {
		Cart cart = cartRepository.findByMember_Id(request.userId()).orElseThrow(CartNotFoundException::new);

		String cartId = MEMBER_CART.withSuffix(request.userId().toString());
		redisCartRepository.setShadowExpireKey(RedisCartKey.SHADOW_KEY.withSuffix(cartId), 60, TimeUnit.SECONDS);
		if (redisCartRepository.isExist(cartId)) {
			return;
		}
		Optional<List<CartDetail>> allByCartId = cartDetailRepository.findAllByCart_Id(cart.getId());
		if (allByCartId.isEmpty()) {
			return;
		}
		List<CartDetail> cartDetailList = allByCartId.get();
		List<RedisCartDetail> redisCartDetails = cartDetailList.stream()
			.map(cartDetail -> new RedisCartDetail(cartDetail.getBook().getId().toString(), cartDetail.getQuantity()))
			.toList();
		redisCartRepository.saveAll(cartId, redisCartDetails);
	}

	private List<CartBookResponse> convertRedisCartList(Map<Long, Integer> cart) {
		return cart.entrySet().stream()
			.map(cartItem -> {
					Book book = bookRepository.findById(cartItem.getKey()).orElseThrow();
					return CartBookResponse.of(book, cartItem.getValue());
				}
			)
			.toList();
	}

	private void refreshExpireKey(String cartId) {
		if (cartId.startsWith(MEMBER_CART.getKey())) {
			redisCartRepository.setShadowExpireKey(RedisCartKey.SHADOW_KEY.withSuffix(cartId), 60, TimeUnit.MINUTES);
			return;
		}
		redisCartRepository.setExpire(cartId, 60, TimeUnit.MINUTES);
	}

}
