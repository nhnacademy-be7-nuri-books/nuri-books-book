package shop.nuribooks.books.cart.customer.entitiy;

import jakarta.persistence.Entity;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerCart implements Serializable {
    private Long bookId;
    private int quantity;
}
