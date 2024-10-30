package shop.nuribooks.books.cart.customer.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import shop.nuribooks.books.cart.customer.RedisTestConfig;
import shop.nuribooks.books.cart.customer.dto.request.CustomerCartAddRequest;
import shop.nuribooks.books.cart.customer.dto.request.CustomerCartRemoveItemRequest;
import shop.nuribooks.books.cart.customer.dto.response.CustomerCartResponse;
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

    private static HashOperations<String, Long, Integer> hashOperations;

    @BeforeEach
    void setUp() {
        hashOperations = redisTemplate.opsForHash();
    }

    @AfterEach
    public void tearDown() {
        // Redis 데이터 비우기
        redisTemplate.getConnectionFactory().getConnection().flushDb();
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

        CustomerCartRemoveItemRequest removeItemRequest = new CustomerCartRemoveItemRequest(sessionId, 1L);
        customerCartService.removeCustomerCartItem(removeItemRequest);
        List<CustomerCartResponse> customerCartList2 = customerCartService.getCustomerCartList(sessionId);

        assertThat(customerCartList2).hasSize(1);
//        customerCartService.removeCustomerCart(sessionId);

    }
}