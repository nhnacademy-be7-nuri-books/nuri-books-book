package shop.nuribooks.books.member.cart.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.entity.BookStateEnum;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.member.cart.dto.request.CartAddRequest;
import shop.nuribooks.books.member.cart.dto.response.CartAddResponse;
import shop.nuribooks.books.member.cart.entity.Cart;
import shop.nuribooks.books.member.cart.entity.CartId;
import shop.nuribooks.books.member.cart.repository.CartRepository;
import shop.nuribooks.books.member.member.entity.AuthorityType;
import shop.nuribooks.books.member.member.entity.GenderType;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.entity.StatusType;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

	@InjectMocks
	private CartServiceImpl cartServiceImpl;

	@Mock
	private CartRepository cartRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private BookRepository bookRepository;


	@DisplayName("회원과 도서의 id로 신규 장바구니 생성 성공")
	@Test
	void addToCart() {
		//given
		Member savedMember = getSavedMember();
		Book savedBook = getSavedBook();
		Cart savedCart = getSavedCart();
		CartAddRequest request = getCartAddRequest();
		CartId cartId = new CartId(request.memberId(), request.bookId());

		when(cartRepository.findById(cartId)).thenReturn(Optional.empty());
		when(memberRepository.findById(request.memberId())).thenReturn(Optional.of(savedMember));
		when(bookRepository.findById(request.bookId())).thenReturn(Optional.of(savedBook));
		when(cartRepository.save(any(Cart.class))).thenReturn(savedCart);

		//when
		CartAddResponse result = cartServiceImpl.addToCart(request);

		//then
		assertThat(result.state()).isEqualTo(savedBook.getState());
		assertThat(result.title()).isEqualTo(savedBook.getTitle());
		assertThat(result.thumbnailImageUrl()).isEqualTo(savedBook.getThumbnailImageUrl());
		assertThat(result.price()).isEqualTo(savedBook.getPrice());
		assertThat(result.discountRate()).isEqualTo(savedBook.getDiscountRate());
	}

	@DisplayName("회원과 도서의 id로 신규 장바구니 생성 실패 - 존재하지 않는 회원")
	@Test
	void addToCart_memberNotFound() {
		//given
		CartAddRequest request = getCartAddRequest();
		CartId cartId = new CartId(request.memberId(), request.bookId());

		when(cartRepository.findById(cartId)).thenReturn(Optional.empty());
		when(memberRepository.findById(request.memberId())).thenReturn(Optional.empty());

		//when / then
		assertThatThrownBy(() -> cartServiceImpl.addToCart(request))
			.isInstanceOf(MemberNotFoundException.class)
			.hasMessage("존재하지 않는 회원입니다.");
	}

	@DisplayName("회원과 도서의 id로 신규 장바구니 생성 실패 - 존재하지 않는 도서")
	@Test
	void addToCart_bookNotFound() {
		//given
		Member savedMember = getSavedMember();
		CartAddRequest request = getCartAddRequest();
		CartId cartId = new CartId(request.memberId(), request.bookId());

		when(cartRepository.findById(cartId)).thenReturn(Optional.empty());
		when(memberRepository.findById(request.memberId())).thenReturn(Optional.of(savedMember));
		when(bookRepository.findById(request.bookId())).thenReturn(Optional.empty());

		// when / then
		assertThatThrownBy(() -> cartServiceImpl.addToCart(request))
			.isInstanceOf(BookNotFoundException.class)
			.hasMessage("해당 도서를 찾을 수 없습니다. Id : " + request.bookId());
	}

	@DisplayName("회원과 도서의 id로 기존 장바구니 도서 수량 증가 성공")
	@Test
	void addToCart_updateQuantity() {
		//given
		Book savedBook = getSavedBook();
		Cart savedCart = spy(getSavedCart());
		Cart quantityIncreasedCart = getQuantityIncreasedCart();
		CartAddRequest request = getCartAddRequest();
		CartId cartId = new CartId(request.memberId(), request.bookId());

		when(cartRepository.findById(cartId)).thenReturn(Optional.of(savedCart));
		when(cartRepository.save(any(Cart.class))).thenReturn(quantityIncreasedCart);

		//when
		CartAddResponse result = cartServiceImpl.addToCart(request);

		//then
		verify(savedCart, times(1)).updateQuantity(request.quantity());

		assertThat(result.state()).isEqualTo(savedBook.getState());
		assertThat(result.title()).isEqualTo(savedBook.getTitle());
		assertThat(result.thumbnailImageUrl()).isEqualTo(savedBook.getThumbnailImageUrl());
		assertThat(result.price()).isEqualTo(savedBook.getPrice());
		assertThat(result.discountRate()).isEqualTo(savedBook.getDiscountRate());
	}

	@DisplayName("기존 장바구니의 도서 수량이 0이 되어 삭제")
	@Test
	void addToCart_deletedByInvalidQuantity() {
		//given
		Cart savedCart = spy(getSavedCart());
		CartAddRequest request = getCartMinusRequest();
		CartId cartId = new CartId(request.memberId(), request.bookId());

		when(cartRepository.findById(cartId)).thenReturn(Optional.of(savedCart));
		doNothing().when(cartRepository).delete(savedCart);

		//when
		CartAddResponse result = cartServiceImpl.addToCart(request);

		//then
		verify(savedCart, times(1)).updateQuantity(request.quantity());

		assertThat(result.state()).isNull();
	}



	/**
	 * 테스트를 위한 회원 생성
	 */
	private Member getSavedMember() {
		return Member.builder()
			.customer(null)
			.authority(AuthorityType.MEMBER)
			.grade(null)
			.status(StatusType.ACTIVE)
			.gender(GenderType.MALE)
			.username("nuribooks95")
			.birthday(LocalDate.of(1988, 8, 12))
			.createdAt(LocalDateTime.now())
			.point(BigDecimal.ZERO)
			.totalPaymentAmount(BigDecimal.ZERO)
			.build();
	}

	/**
	 * 테스트를 위한 도서 생성
	 */
	private Book getSavedBook() {
		return Book.builder()
			.publisherId(null)
			.state(BookStateEnum.NORMAL)
			.title("Original Book Title")
			.thumbnailImageUrl("original_thumbnail.jpg")
			.detailImageUrl("original_detail.jpg")
			.publicationDate(LocalDate.now())
			.price(BigDecimal.valueOf(20000))
			.discountRate(10)
			.description("Original Description")
			.contents("Original Contents")
			.isbn("1234567890123")
			.isPackageable(true)
			.stock(100)
			.likeCount(0)
			.viewCount(0L)
			.build();
	}

	/**
	 * 테스트를 위한 수량 증가 CartAddRequest 생성
	 */
	private CartAddRequest getCartAddRequest() {
		return CartAddRequest.builder()
			.memberId(1L)
			.bookId(1L)
			.quantity(1)
			.build();
	}

	/**
	 * 테스트를 위한 수량 감소 CartAddRequest 생성
	 */
	private CartAddRequest getCartMinusRequest() {
		return CartAddRequest.builder()
			.memberId(1L)
			.bookId(1L)
			.quantity(-3)
			.build();
	}

	/**
	 * 테스트를 위한 장바구니 생성
	 */
	private Cart getSavedCart() {
		return Cart.builder()
			.member(getSavedMember())
			.book(getSavedBook())
			.quantity(1)
			.build();
	}

	/**
	 * 테스트를 위한 수량 증가한 장바구니 생성
	 */
	private Cart getQuantityIncreasedCart() {
		return Cart.builder()
			.member(getSavedMember())
			.book(getSavedBook())
			.quantity(2)
			.build();
	}
}