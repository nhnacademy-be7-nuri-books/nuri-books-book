package shop.nuribooks.books.cart.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.mapper.BookMapper;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.book.service.BookService;
import shop.nuribooks.books.cart.cartdetail.entity.CartDetail;
import shop.nuribooks.books.cart.cartdetail.entity.RedisCartDetail;
import shop.nuribooks.books.cart.cartdetail.repository.CartDetailRepository;
import shop.nuribooks.books.cart.dto.request.CartAddRequest;
import shop.nuribooks.books.cart.dto.request.CartLoadRequest;
import shop.nuribooks.books.cart.dto.response.CartBookResponse;
import shop.nuribooks.books.cart.dto.response.CartResponse;
import shop.nuribooks.books.cart.entity.Cart;
import shop.nuribooks.books.cart.repository.DBCartRepository;
import shop.nuribooks.books.cart.repository.RedisCartRepository;

@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService {
    private static final String SHADOW_KEY = "shadow:";
    private static final String MEMBER_CART_KEY = "member:";

    private final BookRepository bookRepository;
    private final RedisCartRepository redisCartRepository;
    private final DBCartRepository dbCartRepository;
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

    //비회원 장바구니 조회
    @Override
    public List<CartResponse> getCart(String cartId) {
        Map<Long, Integer> cart = redisCartRepository.getCart(cartId);

        return convertRedisCartList(cart);
    }

    //비회원 장바구니 전체 삭제
    @Override
    public void removeCart(String sessionId) {
        //TODO: 장바구니가 없는 경우 예외 처리
       redisCartRepository.removeCart(sessionId);
    }

    //비회원 장바구니 특정 도서 삭제
    @Override
    public void removeCartItem(String sessionId, Long bookId) {
        //TODO: 아이템 없는 경우 예외 처리
       redisCartRepository.removeCartItem(sessionId, bookId.toString());
    }

    @Override
    public void loadCart(CartLoadRequest request) {
        Cart cart = dbCartRepository.findByMember_Id(request.userId()).orElse(null);
        if (Objects.isNull(cart)) {
            return;
        }
        String cartId = MEMBER_CART_KEY + request.userId();
        if (redisCartRepository.isExist(cartId)) {
            redisCartRepository.setShadowExpireKey(SHADOW_KEY + cartId, 1, TimeUnit.MINUTES);
            return;
        }
        List<CartDetail> cartDetailList = cartDetailRepository.findByCart_Id(cart.getId());
        List<RedisCartDetail> redisCartDetailList = cartDetailList.stream()
            .map(cartDetail -> new RedisCartDetail(cartDetail.getBook().getId().toString(), cartDetail.getQuantity())).toList();
        redisCartRepository.saveAll(cartId, redisCartDetailList);
        redisCartRepository.setShadowExpireKey(SHADOW_KEY + cartId, 1, TimeUnit.MINUTES);
    }

    private List<CartResponse> convertRedisCartList(Map<Long, Integer> cart) {
        return cart.entrySet().stream()
            .map(cartItem -> {
                    Book book = bookRepository.findById(cartItem.getKey()).orElseThrow();
                    return new CartResponse(CartBookResponse.of(book), cartItem.getValue());
                }
            )
            .toList();
    }

}
