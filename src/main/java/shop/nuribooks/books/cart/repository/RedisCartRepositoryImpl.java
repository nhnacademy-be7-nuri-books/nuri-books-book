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

    private final HashOperations<String, String, Integer> hashOperations;
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisCartRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void addCart(String cartId, RedisCartDetail redisCartDetail) {
        hashOperations.put(cartId, redisCartDetail.getBookId(), redisCartDetail.getQuantity());
        redisTemplate.expire(cartId, 1, TimeUnit.MINUTES);
    }

    @Override
    public Map<String, Integer> getCart(String cartId) {
        Map<String, Integer> cartItems = new HashMap<>();
        ScanOptions scanOptions = ScanOptions.scanOptions().count(20).build();

        try (Cursor<Map.Entry<String, Integer>> result = hashOperations.scan(cartId, scanOptions)) {
            while (result.hasNext()) {
                Map.Entry<String, Integer> cartItem = result.next();
                cartItems.put(cartItem.getKey(), cartItem.getValue());
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
