package shop.nuribooks.books.cart.customer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import shop.nuribooks.books.cart.customer.dto.request.CustomerCartAddRequest;
import shop.nuribooks.books.cart.customer.service.CustomerCartService;
import shop.nuribooks.books.cart.customer.service.CustomerCartServiceImpl;

@RequiredArgsConstructor
@RestController
public class CustomerCartController {

    private final CustomerCartService customerCartService;

    @PostMapping
    private ResponseEntity addToCart(@RequestBody @Valid CustomerCartAddRequest request) {
        customerCartService.addToCart(request);
        return ResponseEntity.ok().build();
    }

}
