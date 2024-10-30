package shop.nuribooks.books.cart.customer.repository;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import shop.nuribooks.books.cart.customer.entitiy.CustomerCart;

@Repository
public class CustomerCartRepositoryImpl implements CustomerCartRepository{

    private final HashOperations<String, String, Integer> hashOperations;
    private final RedisTemplate<String, Object> redisTemplate;

    public CustomerCartRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void addCart(CustomerCart customerCart) {
        hashOperations.put(customerCart.getId(), customerCart.getBookId(), customerCart.getQuantity());
        redisTemplate.expire(customerCart.getId(), 3, TimeUnit.MINUTES);
    }

    @Override
    public Map<String, Integer> getCart(String sessionId) {
        return hashOperations.entries(sessionId);
    }

    @Override
    public void removeCart(String sessionId) {
        redisTemplate.delete(sessionId);
    }


}
