package shop.nuribooks.books.cart.customer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CartCustomerController {

    private final RedisTemplate<String, String> redisTemplate;


}
