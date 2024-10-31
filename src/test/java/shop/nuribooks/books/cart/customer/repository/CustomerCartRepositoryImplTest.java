package shop.nuribooks.books.cart.customer.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.redis.testcontainers.RedisContainer;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import shop.nuribooks.books.cart.customer.AbstractContainerBaseTest;
import shop.nuribooks.books.cart.customer.entitiy.CustomerCart;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class CustomerCartRepositoryImplTest extends AbstractContainerBaseTest {

    public static final String CART_KEY = "cart:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CustomerCartRepository customerCartRepository;

    private static HashOperations<String, String, Integer> hashOperations;

    @BeforeEach
    void setUp() {
        hashOperations = redisTemplate.opsForHash();
    }

    @AfterEach
    public void tearDown() {
        // Redis 데이터 비우기
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @DisplayName("장바구니에 아이템을 추가한다.")
    @Test
    void addCart() {
        // given
        String sessionId = "sessionId";
        String bookId = "1";
        int quantity = 3;

        CustomerCart customerCart = CustomerCart.builder()
                .id(sessionId)
                .bookId(bookId)
                .quantity(quantity)
                .build();

        // when
        customerCartRepository.addCart(customerCart);

        // then
        Integer savedQuantity = hashOperations.get(sessionId, bookId);
        assertThat(savedQuantity).isEqualTo(3);
    }

    @DisplayName("session에 대한 장바구니를 가져온다.")
    @Test
    void getCart() {
        // given
        String sessionId = "sessionId";

        hashOperations.put(sessionId, "1", 1);
        hashOperations.put(sessionId, "2", 1);

        // when
        Map<String, Integer> cart = customerCartRepository.getCart(sessionId);

        // then
        assertThat(cart).containsEntry("1", 1);
    }
//
//    @DisplayName("카트 전체를 삭제한다.")
//    @Test
//    void removeCart() {
//        // given
//        String sessionId = "sessionId";
//
//        hashOperations.put(sessionId, "1", 1);
//
//        // when
//        customerCartRepository.removeCart(sessionId);
//
//        // then
//       assertThat(redisTemplate.hasKey(sessionId)).isFalse();
//    }
//
//    @DisplayName("장바구니의 특정한 아이템을 삭제한다.")
//    @Test
//    void removeCartItem() {
//        // given
//        String sessionId = "sessionId";
//        String bookId = "1";
//
//        hashOperations.put(sessionId, bookId, 1);
//        hashOperations.put(sessionId, "2", 1);
//
//        // when
//        customerCartRepository.removeCartItem(sessionId, bookId);
//
//        // then
//        assertThat(hashOperations.hasKey(sessionId, bookId)).isFalse();
//    }
  
}