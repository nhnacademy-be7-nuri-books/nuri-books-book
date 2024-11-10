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
import shop.nuribooks.books.member.cart.dto.response.CartUpdateResponse;
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
	 * 회원과 도서의 PK id를 복합키로 가지고 있는 cart 생성한다.
	 * @param memberId 회원의 PK id
	 * @param bookId 도서의 PK id
	 * @param quantity 추가할 도서 수량
	 * @throws InvalidCartQuantityException 도서의 수량은 1권 이상이어야 합니다.
	 * @throws MemberCartNotFoundException 존재하지 않는 회원입니다.
	 * @throws BookNotFoundException 해당 도서를 찾을 수 없습니다 Id :
	 * @return 장바구니에 담은 도서의 정보와 수량을 CartAddResponse에 담아서 반환
	 * 기존 장바구니가 존재한다면 수량만 증가시켜 다시 CartAddResponse로 반환 <br>
	 */
	@Transactional
	@Override
	public CartAddResponse addToCart(Long memberId, Long bookId, int quantity) {

		if (quantity <= 0) {
			throw new InvalidCartQuantityException("도서의 수량은 1권 이상이어야 합니다.");
		}

		CartId cartId = new CartId(memberId, bookId);

		return cartRepository.findById(cartId)
			.map(foundCart -> {
				foundCart.addQuantity(quantity);

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
	 * @param memberId 회원의 PK id
	 * @return 각 장바구니에 담긴 도서의 정보와 수량을 CartListResponse에 담아서 list형식으로 반환
	 * 해당 회원의 장바구니가 없다면 empty list 반환
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
	 * @param memberId 회원의 PK id
	 * @param bookId 도서의 PK id
	 * @throws MemberCartNotFoundException 장바구니가 존재하지 않습니다.
	 */
	@Transactional
	@Override
	public void deleteCart(Long memberId, Long bookId) {

		CartId cartId = new CartId(memberId, bookId);

		Cart foundCart = cartRepository.findById(cartId)
			.orElseThrow(MemberCartNotFoundException::new);

		cartRepository.delete(foundCart);
	}

	/**
	 * 회원과 도서의 PK id로 장바구니 조회 후 수량 변경
	 * @param memberId 회원의 PK id
	 * @param bookId 도서의 PK id
	 * @param quantity 변경할 도서 수량
	 * @throws InvalidCartQuantityException 도서의 수량은 1권 이상이어야 합니다.
	 * @throws MemberCartNotFoundException 장바구니가 존재하지 않습니다.
	 * @return 장바구니에 담은 도서 정보와 수량을 CartUpdateResponse에 담아서 반환
	 */
	@Transactional
	@Override
	public CartUpdateResponse updateCart(Long memberId, Long bookId, int quantity) {

		if (quantity <= 0) {
			throw new InvalidCartQuantityException("도서의 수량은 1권 이상이어야 합니다.");
		}

		CartId cartId = new CartId(memberId, bookId);

		Cart foundCart = cartRepository.findById(cartId)
			.orElseThrow(MemberCartNotFoundException::new);

		foundCart.updateQuantity(quantity);

		return DtoMapper.toCartUpdateDto(foundCart);
	}
}
