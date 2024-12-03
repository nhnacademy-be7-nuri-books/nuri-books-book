package shop.nuribooks.books.cart.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import shop.nuribooks.books.cart.TestRedisContainer;
import shop.nuribooks.books.cart.cartdetail.entity.RedisCartDetail;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class CustomerCartRepositoryImplTest extends TestRedisContainer {

	private static HashOperations<String, String, Integer> hashOperations;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private RedisCartRepository redisCartRepository;

	@BeforeEach
	void setUp() {
		hashOperations = redisTemplate.opsForHash();
	}

	@AfterEach
	public void tearDown() {
		Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().flushDb();
	}

	@DisplayName("장바구니에 아이템을 추가한다.")
	@Test
	void addCart() {
		// given
		String cartId = "cartId";
		String bookId = "1";
		int quantity = 3;
		RedisCartDetail redisCartDetail = new RedisCartDetail(bookId, quantity);

		// when
		redisCartRepository.addCart(cartId, redisCartDetail);

		// then
		Integer savedQuantity = hashOperations.get(cartId, bookId);
		assertThat(savedQuantity).isEqualTo(3);
	}

	@DisplayName("장바구니를 가져온다.")
	@Test
	void getCart() {
		// given
		String cartId = "cartId";

		hashOperations.put(cartId, "1", 1);
		hashOperations.put(cartId, "2", 1);

		// when
		Map<Long, Integer> cart = redisCartRepository.getCart(cartId);

		// then
		assertThat(cart)
			.containsEntry(1L, 1)
			.containsEntry(2L, 1);

	}

	@DisplayName("카트 전체를 삭제한다.")
	@Test
	void removeCart() {
		// given
		String cartId = "cartId";

		hashOperations.put(cartId, "1", 1);

		// when
		redisCartRepository.removeCart(cartId);

		// then
		assertThat(redisTemplate.hasKey(cartId)).isFalse();
	}

	@DisplayName("장바구니의 특정한 아이템을 삭제한다.")
	@Test
	void removeCartItem() {
		// given
		String cartId = "cartId";
		String bookId = "1";
		int quantity = 1;

		hashOperations.put(cartId, bookId, quantity);

		// when
		redisCartRepository.removeCartItem(cartId, bookId);

		// then
		assertThat(hashOperations.hasKey(cartId, bookId)).isFalse();
	}

	@DisplayName("장바구니의 ttl을 설정한다.")
	@Test
	void setExpire() {
		// given
		String cartId = "cartId";
		int value = 10;
		TimeUnit timeUnit = TimeUnit.SECONDS;
		String bookId = "1";
		int quantity = 1;
		hashOperations.put(cartId, bookId, quantity);

		// when
		redisCartRepository.setExpire(cartId, value, timeUnit);

		// then
		Long ttl = redisTemplate.getExpire(cartId);
		assertThat(ttl).isGreaterThanOrEqualTo(9).isLessThanOrEqualTo(10);

	}

	@DisplayName("회원 장바구니의 만료 타이머를 설정한다.")
	@Test
	void setShadowExpireKey() {
		// given
		String key = "key";
		int value = 10;
		TimeUnit timeUnit = TimeUnit.SECONDS;

		// when
		redisCartRepository.setShadowExpireKey(key, value, timeUnit);

		// then
		Long ttl = redisTemplate.getExpire(key);
		assertThat(ttl).isGreaterThanOrEqualTo(9).isLessThanOrEqualTo(10);
	}

	@DisplayName("장바구니 목록을 카트에 저장한다.")
	@Test
	void saveAll() {
		// given
		String cartId = "cartId";
		String bookId1 = "1";
		int quantity1 = 3;
		RedisCartDetail redisCartDetail1 = new RedisCartDetail(bookId1, quantity1);

		String bookId2 = "2";
		int quantity2 = 1;
		RedisCartDetail redisCartDetail2 = new RedisCartDetail(bookId2, quantity2);

		// when
		redisCartRepository.saveAll(cartId, List.of(redisCartDetail1, redisCartDetail2));

		// then
		assertThat(redisTemplate.opsForHash().entries(cartId)).hasSize(2);
	}

	@DisplayName("장바구니가 존재하는지 확인한다.")
	@Test
	void isExist() {
		// given

		// when
		boolean exist = redisCartRepository.isExist("cartId");

		// then
		assertThat(exist).isFalse();
	}
}
