package shop.nuribooks.books.cart.customer.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import shop.nuribooks.books.cart.customer.dto.request.CustomerCartAddRequest;
import shop.nuribooks.books.cart.customer.dto.response.CustomerCartResponse;
import shop.nuribooks.books.cart.customer.service.CustomerCartService;
import shop.nuribooks.books.cart.customer.service.CustomerCartServiceImpl;
import shop.nuribooks.books.exception.cart.customer.CustomerCartNotFoundException;

@RequiredArgsConstructor
@RestController
public class CustomerCartController {

    private final CustomerCartService customerCartService;

    @PostMapping("/api/customer/cart")
    private ResponseEntity addToCart(@RequestBody @Valid CustomerCartAddRequest customerCartAddRequest, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String sessionId = session.getId();

        customerCartService.addToCart(sessionId, customerCartAddRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/customer/cart")
    private ResponseEntity<List<CustomerCartResponse>> getCustomerCartList(HttpServletRequest request) {
        String sessionId = getSessionId(request);
        List<CustomerCartResponse> customerCartList = customerCartService.getCustomerCartList(sessionId);
        return ResponseEntity.ok().body(customerCartList);
    }

    @DeleteMapping("/api/customer/cart")
    private ResponseEntity removeCustomerCart(HttpServletRequest request) {
        String sessionId = getSessionId(request);
        customerCartService.removeCustomerCart(sessionId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/customer/cart/{bookId}")
    private ResponseEntity removeCustomerCartItem(@PathVariable Long bookId,
                                                  HttpServletRequest request) {
        String sessionId = getSessionId(request);
        customerCartService.removeCustomerCartItem(sessionId, bookId);
        return ResponseEntity.noContent().build();
    }

    private String getSessionId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (Objects.isNull(session)) {
            throw new CustomerCartNotFoundException();
        }
        return session.getId();
    }

}
