package shop.nuribooks.books.cart.repository;

import java.util.HashMap;
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
        redisTemplate.expire(cartId, 5, TimeUnit.MINUTES);
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


}
