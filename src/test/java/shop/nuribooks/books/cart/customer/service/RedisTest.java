package shop.nuribooks.books.cart.customer.service;

import static org.assertj.core.api.Assertions.*;


import com.redis.testcontainers.RedisContainer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
//@ExtendWith(SpringExtension.class)
//@DataRedisTest
public class RedisTest {

    @Container
    private static final RedisContainer container = new RedisContainer("redis:7.0.10");

    private static RedisTemplate<String, String> redisTemplate;
//
//    @Configuration
//    static class TestConfig {
//        @Bean
//        public RedisConnectionFactory redisConnectionFactory() {
//            return new LettuceConnectionFactory(container.getHost(), container.getMappedPort(6379));
//        }
//
//        @Bean
//        public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
//            RedisTemplate<String, String> template = new RedisTemplate<>();
//            template.setConnectionFactory(connectionFactory);
//            return template;
//        }
//    }

    @BeforeAll
    static void beforeAll() {
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(container.getHost(),
                container.getMappedPort(6379));
        lettuceConnectionFactory.start();

        redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        redisTemplate.afterPropertiesSet();
    }

    @Test
    void testRedisTemplateOperations() {
        String key = "testKey";
        String value = "testValue";

        // 1. Redis에 값 저장
        redisTemplate.opsForValue().set(key, value);

        // 2. 값 가져오기 및 검증
        String retrievedValue = redisTemplate.opsForValue().get(key);
        assertThat(retrievedValue).isEqualTo(value);

    }


}
