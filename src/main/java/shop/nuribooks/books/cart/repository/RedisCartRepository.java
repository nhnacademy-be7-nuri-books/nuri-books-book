package shop.nuribooks.books.cart.repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import shop.nuribooks.books.cart.cartdetail.entity.CartDetail;
import shop.nuribooks.books.cart.cartdetail.entity.RedisCartDetail;

public interface RedisCartRepository {
    void addCart(String cartId, RedisCartDetail redisCartDetail);

    Map<Long, Integer> getCart(String cartId);

    void removeCart(String cartId);

    void removeCartItem(String cartId, String bookId);

    void setExpire(String cartId, int value, TimeUnit timeUnit);

    void setShadowExpireKey(String key, int value, TimeUnit timeUnit);

    void saveAll(String cartId, List<RedisCartDetail> redisCartDetailList);

    boolean isExist(String cartId);
}
