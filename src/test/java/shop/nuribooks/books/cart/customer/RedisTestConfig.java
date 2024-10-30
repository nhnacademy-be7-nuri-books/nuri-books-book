package shop.nuribooks.books.cart.customer;

import com.redis.testcontainers.RedisContainer;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Profile("test")
@TestConfiguration
public class RedisTestConfig {

    private RedisContainer redisContainer = new RedisContainer("redis:7.0.5");

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        redisContainer = new RedisContainer("redis:7.0.5");
        redisContainer.start();
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

    @PreDestroy
    public void stopRedisContainer() {
        redisContainer.stop();
    }


}
