package shop.nuribooks.books.cart.customer.repository;

import java.util.Map;
import shop.nuribooks.books.cart.customer.entity.CustomerCart;

public interface CustomerCartRepository {
    void addCart(String sessionId, CustomerCart customerCart);

    Map<String, Integer> getCart(String sessionId);

    void removeCart(String sessionId);

    void removeCartItem(String sessionId, String bookId);
}
