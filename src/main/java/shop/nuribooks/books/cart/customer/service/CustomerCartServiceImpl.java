package shop.nuribooks.books.cart.customer.service;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import shop.nuribooks.books.cart.customer.dto.request.CustomerCartAddRequest;
import shop.nuribooks.books.cart.customer.dto.response.CustomerCartResponse;

@Service
public class CustomerCartServiceImpl implements CustomerCartService{
    public static final String CART_KEY = "cart:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, Integer> hashOperations;

    public CustomerCartServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        hashOperations = redisTemplate.opsForHash();
    }

    // 비회원 장바구니 담기
    @Override
    public void addToCart(CustomerCartAddRequest request) {
        String cartKey = CART_KEY + request.sessionId();
        String bookId = String.valueOf(request.bookId());
        int quantity = request.quantity();
        hashOperations.put(cartKey, bookId, quantity);
    }

    // 비회원 장바구니 조회
//    @Override
//    public List<CustomerCartResponse> getCustomerCartList(Long sessionId) {
//        Map<String, Integer> entries = hashOperations.entries(CART_KEY + sessionId);
//    }

}
