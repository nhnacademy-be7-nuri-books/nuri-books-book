package shop.nuribooks.books.member.cart.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.member.cart.dto.request.CartAddRequest;
import shop.nuribooks.books.member.cart.dto.response.CartAddResponse;
import shop.nuribooks.books.member.cart.dto.DtoMapper;
import shop.nuribooks.books.member.cart.dto.response.CartListResponse;
import shop.nuribooks.books.member.cart.entity.Cart;
import shop.nuribooks.books.member.cart.entity.CartId;
import shop.nuribooks.books.member.cart.repository.CartRepository;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;

/**
 * @author Jprotection
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepository;
	private final MemberRepository memberRepository;
	private final BookRepository bookRepository;

	/**
	 * 장바구니 상세 생성 <br>
	 * memberId와 bookId를 복합키로 가지고 있는 cart 생성 후, <br>
	 * 해당 도서의 상세 정보들을 CartAddResponse에 담아서 반환 <br>
	 * 기존 장바구니가 존재한다면 수량만 하나 증가시켜 다시 CartAddResponse로 반환
	 */
	@Transactional
	public CartAddResponse addToCart(CartAddRequest request) {
		CartId cartId = new CartId(request.memberId(), request.bookId());

		return cartRepository.findById(cartId)
			.map(cart -> {
				cart.updateQuantity(request.quantity());

				if (cart.getQuantity() <= 0) {
					cartRepository.delete(cart);
					return DtoMapper.toCartNullDto();
				}

				Cart quantityUpdatedCart = cartRepository.save(cart);
				return DtoMapper.toCartAddDto(quantityUpdatedCart);
			})
			.orElseGet(() -> {
				Member foundMember = memberRepository.findById(request.memberId())
					.orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));
				Book foundBook = bookRepository.findById(request.bookId())
					.orElseThrow(() -> new BookNotFoundException(request.bookId())); // "해당 도서를 찾을 수 없습니다. Id : "

				Cart newCart = Cart.builder()
					.id(cartId)
					.member(foundMember)
					.book(foundBook)
					.quantity(request.quantity())
					.updatedAt(LocalDateTime.now())
					.build();

				Cart savedCart = cartRepository.save(newCart);

				return DtoMapper.toCartAddDto(savedCart);
			});
	}

	@Override
	public List<CartListResponse> getCartList(Long memberId) {
		List<Cart> carts = cartRepository.findAllByMemberId(memberId);

		if (carts.isEmpty()) {
			return List.of(DtoMapper.toCartListNullDto());
		}

		return carts.stream()
			.map(DtoMapper::toCartListDto)
			.toList();
	}
}
