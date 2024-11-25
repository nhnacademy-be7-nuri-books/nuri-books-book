package shop.nuribooks.books.cart.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Repository;

import shop.nuribooks.books.cart.cartdetail.entity.RedisCartDetail;

@Repository
public class RedisCartRepositoryImpl implements RedisCartRepository {

	private final HashOperations<String, Object, Object> hashOperations;
	private final RedisTemplate<String, Object> redisTemplate;

	public RedisCartRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
		this.hashOperations = redisTemplate.opsForHash();
	}

	@Override
	public void addCart(String cartId, RedisCartDetail redisCartDetail) {
		hashOperations.put(cartId, redisCartDetail.getBookId(), redisCartDetail.getQuantity());
	}

	@Override
	public Map<Long, Integer> getCart(String cartId) {
		Map<Long, Integer> cartItems = new HashMap<>();
		ScanOptions scanOptions = ScanOptions.scanOptions().count(20).build();

		try (Cursor<Map.Entry<Object, Object>> result = hashOperations.scan(cartId, scanOptions)) {
			while (result.hasNext()) {
				Map.Entry<Object, Object> cartItem = result.next();
				Long bookId = Long.parseLong(cartItem.getKey().toString());
				Integer quantity = Integer.valueOf(cartItem.getValue().toString());
				cartItems.put(bookId, quantity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cartItems;
	}

	@Override
	public void removeCart(String sessionId) {
		redisTemplate.delete(sessionId);
	}

	@Override
	public void removeCartItem(String sessionId, String bookId) {
		hashOperations.delete(sessionId, bookId);
	}

	@Override
	public void setExpire(String cartId, int value, TimeUnit timeUnit) {
		redisTemplate.expire(cartId, value, timeUnit);
	}

	@Override
	public void setShadowExpireKey(String key, int value, TimeUnit timeUnit) {
		redisTemplate.opsForValue().set(key, "");
		redisTemplate.expire(key, value, timeUnit);
	}

	@Override
	public void saveAll(String cartId, List<RedisCartDetail> redisCartDetailList) {
		redisCartDetailList.forEach(
			redisCartDetail -> hashOperations.put(cartId, redisCartDetail.getBookId(), redisCartDetail.getQuantity()));
	}

	@Override
	public boolean isExist(String cartId) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(cartId));
	}

}
