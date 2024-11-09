package shop.nuribooks.books.member.cart.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.member.InvalidCartQuantityException;
import shop.nuribooks.books.exception.member.MemberCartNotFoundException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
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
	 * 장바구니 생성 <br>
	 * 회원과 도서의 PK id를 복합키로 가지고 있는 cart 생성 후, <br>
	 * 해당 도서의 상세 정보들을 CartAddResponse에 담아서 반환 <br>
	 * 기존 장바구니가 존재한다면 수량만 변화시켜 다시 CartAddResponse로 반환 <br>
	 */
	@Transactional
	@Override
	public CartAddResponse addToCart(Long memberId, Long bookId, int quantity) {

		if (quantity <= 0) {
			throw new InvalidCartQuantityException("도서의 수량은 1개 이상이어야 합니다.");
		}

		CartId cartId = new CartId(memberId, bookId);

		return cartRepository.findById(cartId)
			.map(foundCart -> {
				foundCart.updateQuantity(quantity);

				return DtoMapper.toCartAddDto(foundCart);
			})
			.orElseGet(() -> {
				Member foundMember = memberRepository.findById(memberId)
					.orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));
				Book foundBook = bookRepository.findById(bookId)
					.orElseThrow(() -> new BookNotFoundException(bookId)); // "해당 도서를 찾을 수 없습니다. Id : "

				Cart newCart = Cart.builder()
					.id(cartId)
					.member(foundMember)
					.book(foundBook)
					.quantity(quantity)
					.updatedAt(LocalDateTime.now())
					.build();

				Cart savedCart = cartRepository.save(newCart);

				return DtoMapper.toCartAddDto(savedCart);
			});
	}

	/**
	 * 회원의 PK id로 모든 장바구니 조회 <br>
	 * 해당 회원의 장바구니가 없다면 모든 필드가 null인 CartListResponse 반환
	 */
	@Override
	public List<CartListResponse> getCartList(Long memberId) {
		List<Cart> carts = cartRepository.findAllByMemberId(memberId);

		if (carts.isEmpty()) {
			return List.of();
		}

		return carts.stream()
			.map(DtoMapper::toCartListDto)
			.toList();
	}

	/**
	 * 회원과 도서의 PK id로 장바구니 조회 후 삭제
	 */
	@Transactional
	@Override
	public void deleteCart(Long memberId, Long bookId) {

		CartId cartId = new CartId(memberId, bookId);

		Cart foundCart = cartRepository.findById(cartId)
			.orElseThrow(() -> new MemberCartNotFoundException("장바구니가 존재하지 않습니다."));

		cartRepository.delete(foundCart);
	}
}
