package shop.nuribooks.books.cart.repository;

import java.util.Map;

import shop.nuribooks.books.cart.cartdetail.entity.RedisCartDetail;

public interface RedisCartRepository {
    void addCart(String cartId, RedisCartDetail redisCartDetail);

    Map<String, Integer> getCart(String sessionId);

    void removeCart(String sessionId);

    void removeCartItem(String sessionId, String bookId);
}
