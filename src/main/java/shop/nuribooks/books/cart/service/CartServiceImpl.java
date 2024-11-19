package shop.nuribooks.books.cart.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.cart.cartdetail.entity.CartDetail;
import shop.nuribooks.books.cart.cartdetail.entity.RedisCartDetail;
import shop.nuribooks.books.cart.cartdetail.repository.CartDetailRepository;
import shop.nuribooks.books.cart.dto.request.CartAddRequest;
import shop.nuribooks.books.cart.dto.request.CartLoadRequest;
import shop.nuribooks.books.cart.dto.response.CartBookResponse;
import shop.nuribooks.books.cart.entity.Cart;
import shop.nuribooks.books.cart.repository.CartRepository;
import shop.nuribooks.books.cart.repository.RedisCartRepository;
import shop.nuribooks.books.exception.cart.CartNotFoundException;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService {
    private static final String SHADOW_KEY = "expire:timer:";
    private static final String MEMBER_CART_KEY = "member:";

    private final BookRepository bookRepository;
    private final RedisCartRepository redisCartRepository;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;

    @Override
    public void addCustomerCart(String cartId, CartAddRequest request) {
        RedisCartDetail redisCartDetail = new RedisCartDetail(request.bookId().toString(), request.quantity());
        redisCartRepository.addCart(cartId, redisCartDetail);
        redisCartRepository.setExpire(cartId, 1, TimeUnit.MINUTES);
    }

    @Override
    public void addMemberCart(String cartId, CartAddRequest request) {
        RedisCartDetail redisCartDetail = new RedisCartDetail(request.bookId().toString(), request.quantity());
        redisCartRepository.addCart(cartId, redisCartDetail);
        redisCartRepository.setShadowExpireKey(SHADOW_KEY + cartId, 1, TimeUnit.MINUTES);
    }

    @Override
    public List<CartBookResponse> getCart(String cartId) {
        Map<Long, Integer> cart = redisCartRepository.getCart(cartId);
        refreshExpireKey(cartId);
        return convertRedisCartList(cart);
    }

    @Override
    public void removeCart(String cartId) {
       redisCartRepository.removeCart(cartId);
    }

    //비회원 장바구니 특정 도서 삭제
    @Override
    public void removeCartItem(String cartId, Long bookId) {
       redisCartRepository.removeCartItem(cartId, bookId.toString());
    }

    @Override
    public void loadCart(CartLoadRequest request) {
        Cart cart = cartRepository.findByMember_Id(request.userId()).orElseThrow(CartNotFoundException::new);
        if (Objects.isNull(cart)) {
            return;
        }
        String cartId = MEMBER_CART_KEY + request.userId();
        if (redisCartRepository.isExist(cartId)) {
            redisCartRepository.setShadowExpireKey(SHADOW_KEY + cartId, 1, TimeUnit.SECONDS);
            return;
        }
        Optional<List<CartDetail>> allByCartId = cartDetailRepository.findAllByCart_Id(cart.getId());
        if (allByCartId.isEmpty()) {
            return;
        }
        List<CartDetail> cartDetailList = allByCartId.get();
        List<RedisCartDetail> redisCartDetails = cartDetailList.stream()
            .map(cartDetail -> new RedisCartDetail(cartDetail.getBook().getId().toString(), cartDetail.getQuantity()))
            .toList();
        redisCartRepository.saveAll(cartId, redisCartDetails);
        redisCartRepository.setShadowExpireKey(SHADOW_KEY + cartId, 1, TimeUnit.MINUTES);
    }

    private List<CartBookResponse> convertRedisCartList(Map<Long, Integer> cart) {
        return cart.entrySet().stream()
            .map(cartItem -> {
                    Book book = bookRepository.findById(cartItem.getKey()).orElseThrow();
                    return CartBookResponse.of(book, cartItem.getValue());
                }
            )
            .toList();
    }

    private void refreshExpireKey(String cartId) {
        if (cartId.startsWith(MEMBER_CART_KEY)) {
            redisCartRepository.setShadowExpireKey(SHADOW_KEY + cartId, 1, TimeUnit.MINUTES);
        }
        else {
            redisCartRepository.setExpire(cartId, 1, TimeUnit.MINUTES);

        }
    }

}
