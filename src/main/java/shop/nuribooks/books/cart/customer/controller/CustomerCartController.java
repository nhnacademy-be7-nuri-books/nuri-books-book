package shop.nuribooks.books.cart.customer.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.nuribooks.books.cart.customer.service.CustomerCartService;

@RequiredArgsConstructor
@RestController
public class CustomerCartController {

    private final CustomerCartService customerCartService;

//    @PostMapping
//    private ResponseEntity addToCart(HttpServletRequest request) {
//
//    }

    @GetMapping("/redis/test")
    private ResponseEntity test() {
        customerCartService.addToCart(1, 1);
        return ResponseEntity.ok().build();
    }
}
