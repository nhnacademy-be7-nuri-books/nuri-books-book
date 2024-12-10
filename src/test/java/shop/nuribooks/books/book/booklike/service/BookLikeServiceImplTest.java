package shop.nuribooks.books.book.booklike.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.entity.BookStateEnum;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.book.utility.BookUtils;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;
import shop.nuribooks.books.book.bookcontributor.repository.BookContributorRepository;
import shop.nuribooks.books.book.booklike.dto.BookLikeResponse;
import shop.nuribooks.books.book.booklike.entity.BookLike;
import shop.nuribooks.books.book.booklike.entity.BookLikeId;
import shop.nuribooks.books.book.booklike.repository.BookLikeRepository;
import shop.nuribooks.books.book.booklike.service.impl.BookLikeServiceImpl;
import shop.nuribooks.books.book.publisher.entity.Publisher;
import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.exception.InvalidPageRequestException;
import shop.nuribooks.books.exception.booklike.BookLikeIdNotFoundException;
import shop.nuribooks.books.exception.booklike.ResourceAlreadyExistBookLikeIdException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class BookLikeServiceImplTest {

	@InjectMocks
	private BookLikeServiceImpl bookLikeService;

	@Mock
	private BookLikeRepository bookLikeRepository;

	@Mock
	private BookContributorRepository bookContributorRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private BookRepository bookRepository;

	private Publisher publisher;
	private Book book;
	private Member member;
	private BookLikeId bookLikeId;
	private BookLike bookLike;

	@BeforeEach
	void setUp() {
		publisher = Publisher.builder()
			.name("누리북스")
			.build();

		book = Book.builder()
			.publisherId(publisher)
			.price(BigDecimal.valueOf(20000))
			.discountRate(10)
			.stock(100)
			.state(BookStateEnum.NORMAL)
			.thumbnailImageUrl("original_thumbnail.jpg")
			.description("Original Description")
			.contents("Original Contents")
			.isPackageable(true)
			.likeCount(0)
			.viewCount(0L)
			.build();

		ReflectionTestUtils.setField(book, "id", 1L);

		member = TestUtils.createMember(TestUtils.createCustomer(), TestUtils.creategrade());
		TestUtils.setIdForEntity(member, 1L);

		bookLikeId = new BookLikeId(member.getId(), book.getId());
		bookLike = new BookLike(bookLikeId, book);
	}

	@DisplayName("특정 도서 좋아요 추가")
	@Test
	void addLike() {
		when(memberRepository.existsById(anyLong())).thenReturn(true);
		when(bookRepository.findByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.of(book));
		when(bookLikeRepository.existsById(bookLikeId)).thenReturn(false);

		when(bookLikeRepository.save(any(BookLike.class))).thenReturn(bookLike);

		bookLikeService.addLike(member.getId(), book.getId());

		verify(memberRepository, times(1)).existsById(anyLong());
		verify(bookRepository, times(1)).findByIdAndDeletedAtIsNull(book.getId());
		verify(bookLikeRepository, times(1)).existsById(bookLikeId);
		verify(bookLikeRepository, times(1)).save(any(BookLike.class));

		assertEquals(1, book.getLikeCount());
	}

	@DisplayName("특정 도서 좋아요 추가 - 회원이 아닐 경우")
	@Test
	void addLikeWhenIsNotMember() {
		when(memberRepository.existsById(anyLong())).thenReturn(false);

		assertThrows(MemberNotFoundException.class, () -> {
			bookLikeService.addLike(member.getId(), book.getId());
		});

		verify(memberRepository, times(1)).existsById(anyLong());
		verify(bookRepository, never()).findByIdAndDeletedAtIsNull(anyLong());
		verify(bookLikeRepository, never()).existsById(bookLikeId);
		verify(bookLikeRepository, never()).save(any(BookLike.class));
	}

	@DisplayName("특정 도서 좋아요 추가 - 이미 좋아요를 눌렀을 경우")
	@Test
	void addLikeWhenLikeIsAlreadyAdd() {
		when(memberRepository.existsById(anyLong())).thenReturn(true);
		when(bookRepository.findByIdAndDeletedAtIsNull(book.getId())).thenReturn(Optional.of(book));
		when(bookLikeRepository.existsById(bookLikeId)).thenReturn(true);

		assertThrows(ResourceAlreadyExistBookLikeIdException.class, () -> {
			bookLikeService.addLike(member.getId(), book.getId());
		});

		verify(memberRepository, times(1)).existsById(member.getId());
		verify(bookRepository, times(1)).findByIdAndDeletedAtIsNull(book.getId());
		verify(bookLikeRepository, times(1)).existsById(bookLikeId);
		verify(bookLikeRepository, never()).save(any(BookLike.class));
	}

	@DisplayName("특정 도서 좋아요 취소")
	@Test
	void removeLike() {
		when(bookLikeRepository.existsById(bookLikeId)).thenReturn(true);
		when(bookRepository.findByIdAndDeletedAtIsNull(book.getId())).thenReturn(Optional.of(book));

		bookLikeService.removeLike(member.getId(), book.getId());

		verify(bookLikeRepository, times(1)).existsById(bookLikeId);
		verify(bookRepository, times(1)).findByIdAndDeletedAtIsNull(book.getId());
		verify(bookLikeRepository, times(1)).deleteById(bookLikeId);
	}

	@DisplayName("특정 도서 좋아요 취소 - 회원의 좋아요 목록에 없는 도서일 경우")
	@Test
	void removeLikeNotFound() {
		when(bookLikeRepository.existsById(bookLikeId)).thenReturn(false);

		assertThrows(BookLikeIdNotFoundException.class, () -> {
			bookLikeService.removeLike(member.getId(), book.getId());
		});

		verify(bookLikeRepository, times(1)).existsById(bookLikeId);
		verify(bookRepository, never()).findByIdAndDeletedAtIsNull(book.getId());
		verify(bookLikeRepository, never()).deleteById(bookLikeId);
	}

	@DisplayName("특정 회원의 좋아요 누른 도서 목록 반환")
	@Test
	void getLikedBooks() {
		Long memberId = member.getId();
		Pageable pageable = PageRequest.of(0, 10);

		Book book2 = Book.builder()
			.publisherId(publisher)
			.price(BigDecimal.valueOf(20000))
			.state(BookStateEnum.NORMAL)
			.thumbnailImageUrl("thumbnail2.jpg")
			.title("Book Title 2")
			.build();

		ReflectionTestUtils.setField(book2, "id", 2L);

		BookLikeResponse bookLikeResponse1 = BookLikeResponse.builder()
			.bookId(book.getId())
			.title(book.getTitle())
			.publisherName(book.getPublisherId().getName())
			.price(book.getPrice())
			.discountRate(book.getDiscountRate())
			.salePrice(BookUtils.calculateSalePrice(book.getPrice(), book.getDiscountRate()))
			.thumbnailImageUrl(book.getThumbnailImageUrl())
			.contributorsByRole(null)
			.build();

		BookLikeResponse bookLikeResponse2 = BookLikeResponse.builder()
			.bookId(book2.getId())
			.title(book2.getTitle())
			.publisherName(book2.getPublisherId().getName())
			.price(book2.getPrice())
			.discountRate(book2.getDiscountRate())
			.salePrice(BookUtils.calculateSalePrice(book2.getPrice(), book2.getDiscountRate()))
			.thumbnailImageUrl(book2.getThumbnailImageUrl())
			.contributorsByRole(null)
			.build();

		List<BookLikeResponse> bookLikeResponses = List.of(bookLikeResponse1, bookLikeResponse2);
		Page<BookLikeResponse> bookLikePage = new PageImpl<>(bookLikeResponses, pageable, bookLikeResponses.size());

		when(memberRepository.existsById(memberId)).thenReturn(true);
		when(bookLikeRepository.findLikedBooks(memberId, pageable)).thenReturn(bookLikePage);

		List<BookContributorInfoResponse> contributors1 = List.of(
			new BookContributorInfoResponse(1L, "카트보이", 1L, "지은이")
		);

		List<BookContributorInfoResponse> contributors2 = List.of(
			new BookContributorInfoResponse(2L, "멤버보이", 1L, "지은이")
		);

		when(bookContributorRepository.findContributorsAndRolesByBookId(bookLikeResponse1.bookId()))
			.thenReturn(contributors1);
		when(bookContributorRepository.findContributorsAndRolesByBookId(bookLikeResponse2.bookId()))
			.thenReturn(contributors2);

		Page<BookLikeResponse> result = bookLikeService.getLikedBooks(memberId, pageable);

		verify(bookLikeRepository, times(1)).findLikedBooks(memberId, pageable);
		verify(bookContributorRepository, times(1)).findContributorsAndRolesByBookId(bookLikeResponse1.bookId());
		verify(bookContributorRepository, times(1)).findContributorsAndRolesByBookId(bookLikeResponse2.bookId());

		assertNotNull(result);
		assertEquals(2, result.getContent().size());
	}

	@DisplayName("특정 회원의 좋아요 누른 도서 목록 반환 - 유효하지 않은 페이지 번호")
	@Test
	void getLikedBooksWithInvalidPageNumber() {
		Long memberId = member.getId();
		Pageable pageable = mock(Pageable.class);

		when(pageable.getPageNumber()).thenReturn(-1);

		InvalidPageRequestException exception = assertThrows(InvalidPageRequestException.class,
			() -> bookLikeService.getLikedBooks(memberId, pageable));
		assertEquals("조회 가능한 페이지 범위를 벗어났습니다.", exception.getMessage());
	}

	@DisplayName("특정 회원의 좋아요 누른 도서 목록 반환 - 존재하지 않는 회원")
	@Test
	void getLikedBooksWithNonExistentMember() {
		Long memberId = member.getId();
		Pageable pageable = PageRequest.of(0, 10);

		when(memberRepository.existsById(memberId)).thenReturn(false);

		assertThrows(MemberNotFoundException.class, () -> {
			bookLikeService.getLikedBooks(memberId, pageable);
		});

		verify(memberRepository, times(1)).existsById(memberId);
		verify(bookLikeRepository, never()).findLikedBooks(anyLong(), any(Pageable.class));
		verify(bookContributorRepository, never()).findContributorsAndRolesByBookId(anyLong());
	}

	@DisplayName("특정 회원의 특정 도서에 대한 좋아요 여부 확인")
	@Test
	void isBookLikedByMember() {
		when(bookLikeRepository.existsByMemberIdAndBookId(member.getId(), book.getId())).thenReturn(true);

		boolean isLiked = bookLikeService.isBookLikedByMember(member.getId(), book.getId());

		verify(bookLikeRepository, times(1)).existsByMemberIdAndBookId(member.getId(), book.getId());

		assertTrue(isLiked);
	}
}
