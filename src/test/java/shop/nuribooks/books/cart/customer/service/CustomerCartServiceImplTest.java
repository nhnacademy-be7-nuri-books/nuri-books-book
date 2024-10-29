package shop.nuribooks.books.cart.customer.service;

import static org.assertj.core.api.Assertions.*;

import com.redis.testcontainers.RedisContainer;
import java.util.Map;
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

@SpringBootTest
class CustomerCartServiceImplTest {

    private static final RedisContainer redisContainer = new RedisContainer("redis:7.0.5");

    static {
        redisContainer.start();
    }

    @TestConfiguration
    static class RedisTestConfig {

        @Bean
        public RedisConnectionFactory redisConnectionFactory() {
            return new LettuceConnectionFactory(redisContainer.getHost(), redisContainer.getFirstMappedPort());
        }

        @Bean
        public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
            RedisTemplate<String, Object> sessionRedisTemplate = new RedisTemplate<>();
            sessionRedisTemplate.setConnectionFactory(redisConnectionFactory);
            sessionRedisTemplate.setKeySerializer(new StringRedisSerializer());
            sessionRedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
            sessionRedisTemplate.setHashKeySerializer(new StringRedisSerializer());
            sessionRedisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
            return sessionRedisTemplate;
        }
    }


    public static final String CART_KEY = "cart:";

    @Autowired
    private CustomerCartService customerCartService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private HashOperations<String, Long, Integer> hashOperations;

    @BeforeEach
    void setUp() {
        hashOperations = redisTemplate.opsForHash();
    }

    @AfterAll
    static void tearDown() {
        if (redisContainer != null) {
            redisContainer.stop();
        }
    }



    @Test
    void testAddToCart() {
        // given
        String sessionId = "sessionId";
        CustomerCartAddRequest request = new CustomerCartAddRequest(sessionId, 1L, 5);

        // when
        customerCartService.addToCart(request);

        // then
        Map<Long, Integer> entries = hashOperations.entries(CART_KEY + sessionId);
        assertThat(entries.size()).isEqualTo(1);

    }
}