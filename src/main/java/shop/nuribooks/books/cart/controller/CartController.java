package shop.nuribooks.books.cart.controller;

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
import shop.nuribooks.books.cart.dto.request.CartAddRequest;
import shop.nuribooks.books.cart.dto.response.CartResponse;
import shop.nuribooks.books.cart.service.CartService;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;
import shop.nuribooks.books.exception.cart.customer.CustomerCartNotFoundException;

@RequiredArgsConstructor
@RestController
public class CartController {
    private static final String MEMBER_CART_KEY = "member:";
    private static final String CUSTOMER_CART_KEY = "customer:";

    private final CartService cartService;

    @PostMapping("/api/cart")
    private ResponseEntity addToCart(@RequestBody @Valid CartAddRequest cartAddRequest) {
        String cartId;
        Long memberId = MemberIdContext.getMemberId();
        // 비회원인 경우
        if (Objects.isNull(memberId)) {
            cartId = CUSTOMER_CART_KEY + cartAddRequest.cartId();
            cartService.addCustomerCart(cartId, cartAddRequest);
        } else {
            cartId = MEMBER_CART_KEY + memberId;
            cartService.addMemberCart(cartId, cartAddRequest);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/cart/{cart-id}")
    private ResponseEntity<List<CartResponse>> getCartList(@PathVariable("cart-id") String requestCartId) {
        Long memberId = MemberIdContext.getMemberId();
        String cartId;
        if (Objects.isNull(memberId)) {
            cartId = CUSTOMER_CART_KEY + requestCartId;
        } else {
            cartId = MEMBER_CART_KEY + requestCartId;
        }
        List<CartResponse> cartResponseList = cartService.getCart(cartId);
        return ResponseEntity.ok().body(cartResponseList);
    }

    @DeleteMapping("/api/customer/cart")
    private ResponseEntity removeCustomerCart(HttpServletRequest request) {
        String sessionId = getSessionId(request);
        cartService.removeCart(sessionId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/customer/cart/{bookId}")
    private ResponseEntity removeCustomerCartItem(@PathVariable Long bookId,
                                                  HttpServletRequest request) {
        String sessionId = getSessionId(request);
        cartService.removeCartItem(sessionId, bookId);
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
