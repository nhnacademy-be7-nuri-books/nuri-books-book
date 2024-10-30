package shop.nuribooks.books.member.cart.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.member.cart.dto.CartAddResponse;
import shop.nuribooks.books.member.cart.dto.DtoMapper;
import shop.nuribooks.books.member.cart.entity.Cart;
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
	 * memberId와 bookId를 복합키로 가지고 있는 cart 생성
	 */
	@Transactional
	public CartAddResponse addToCart(Long memberId, Long bookId) {
		Member foundMember = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));
		Book foundBook = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException(bookId)); // "해당 도서를 찾을 수 없습니다. Id : "

		Cart newCart = Cart.builder()
			.member(foundMember)
			.book(foundBook)
			.quantity(1)
			.build();

		Cart savedCart = cartRepository.save(newCart);

		return DtoMapper.toCartAddDto(savedCart);
	}

}
