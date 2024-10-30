package shop.nuribooks.books.cart.customer.service;

import static org.assertj.core.api.Assertions.*;

import com.redis.testcontainers.RedisContainer;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import shop.nuribooks.books.cart.customer.dto.request.CustomerCartAddRequest;
import shop.nuribooks.books.cart.customer.dto.response.CustomerCartResponse;
import shop.nuribooks.books.cart.customer.entitiy.CustomerCart;
import shop.nuribooks.books.cart.customer.repository.CustomerCartRepository;

@Slf4j
@SpringBootTest
@Import(RedisTestConfig.class)
class CustomerCartServiceImplTest {
    public static final String CART_KEY = "cart:";

    @Autowired
    private CustomerCartService customerCartService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CustomerCartRepository customerCartRepository;

    private HashOperations<String, Long, Integer> hashOperations;

    @BeforeEach
    void setUp() {
        hashOperations = redisTemplate.opsForHash();
    }

    @Test
    void testAddToCart() {
        // given
        String sessionId = "sessionId";
        CustomerCartAddRequest request = new CustomerCartAddRequest(sessionId, 1L, 5);
        CustomerCartAddRequest request2 = new CustomerCartAddRequest(sessionId, 2L, 5);

        // when
        customerCartService.addToCart(request);
        customerCartService.addToCart(request2);

        // then
        List<CustomerCartResponse> customerCartList = customerCartService.getCustomerCartList(sessionId);
        assertThat(customerCartList).extracting("bookId", "quantity")
                .contains(tuple(1L, 5));

//        customerCartService.removeCustomerCart(sessionId);

    }
}