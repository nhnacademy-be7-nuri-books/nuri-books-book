package shop.nuribooks.books.cart.customer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import shop.nuribooks.books.cart.customer.dto.request.CustomerCartAddRequest;

@RequiredArgsConstructor
@Service
public class CustomerCartServiceImpl implements CustomerCartService{
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void addToCart(CustomerCartAddRequest request) {
        HashOperations<String, Object, Object> stringObjectObjectHashOperations = redisTemplate.opsForHash();

    }
}
