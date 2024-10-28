package shop.nuribooks.books.cart.customer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomerCartService {
    private final RedisTemplate<String, String> redisTemplate;

    public void addToCart(int sessionId, int bookId) {
        redisTemplate.opsForValue().set("test", "Test");
    }

}
