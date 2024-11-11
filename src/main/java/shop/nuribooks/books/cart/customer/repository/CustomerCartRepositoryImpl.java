package shop.nuribooks.books.cart.customer.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Repository;
import shop.nuribooks.books.cart.customer.entity.CustomerCart;

@Repository
public class CustomerCartRepositoryImpl implements CustomerCartRepository{

    private final HashOperations<String, String, Integer> hashOperations;
    private final RedisTemplate<String, Object> redisTemplate;

    public CustomerCartRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void addCart(String sessionId, CustomerCart customerCart) {
        hashOperations.put(sessionId, customerCart.getBookId(), customerCart.getQuantity());
        redisTemplate.expire(customerCart.getId(), 3, TimeUnit.MINUTES);
    }

    @Override
    public Map<String, Integer> getCart(String sessionId) {
        Map<String, Integer> cartItems = new HashMap<>();
        ScanOptions scanOptions = ScanOptions.scanOptions().count(20).build();

        try (Cursor<Map.Entry<String, Integer>> result = hashOperations.scan(sessionId, scanOptions)) {
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
