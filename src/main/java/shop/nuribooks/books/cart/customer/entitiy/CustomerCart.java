package shop.nuribooks.books.cart.customer.entitiy;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerCart implements Serializable {

    @Id
    private String id;
    private String bookId;
    private int quantity;

    @Builder
    private CustomerCart(String id, String bookId, int quantity) {
        this.id = id;
        this.bookId = bookId;
        this.quantity = quantity;
    }

}
