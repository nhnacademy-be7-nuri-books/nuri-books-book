package shop.nuribooks.books.cart.customer.repository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import shop.nuribooks.books.cart.customer.entitiy.CustomerCart;

public interface CustomerCartRepository {
    void addCart(CustomerCart customerCart);

    Map<String, Integer> getCart(String sessionId);

    void removeCart(String sessionId);

    void removeCartItem(String sessionId, String bookId);
}
