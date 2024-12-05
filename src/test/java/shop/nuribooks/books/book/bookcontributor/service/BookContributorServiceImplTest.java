package shop.nuribooks.books.book.bookcontributor.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.entity.BookStateEnum;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorRegisterRequest;
import shop.nuribooks.books.book.bookcontributor.entity.BookContributor;
import shop.nuribooks.books.book.bookcontributor.repository.BookContributorRepository;
import shop.nuribooks.books.book.contributor.entity.Contributor;
import shop.nuribooks.books.book.contributor.entity.ContributorRole;
import shop.nuribooks.books.book.contributor.entity.ContributorRoleEnum;
import shop.nuribooks.books.book.contributor.repository.ContributorRepository;
import shop.nuribooks.books.book.contributor.repository.role.ContributorRoleRepository;
import shop.nuribooks.books.book.publisher.entity.Publisher;
import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.contributor.BookContributorNotFoundException;
import shop.nuribooks.books.exception.contributor.ContributorNotFoundException;
import shop.nuribooks.books.exception.contributor.ContributorRoleNotFoundException;

class BookContributorServiceImplTest {

	@InjectMocks
	private BookContributorServiceImpl bookContributorService;

	@Mock
	private BookContributorRepository bookContributorRepository;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private ContributorRepository contributorRepository;

	@Mock
	private ContributorRoleRepository contributorRoleRepository;

	private Book book;
	private Contributor contributor;
	private ContributorRole contributorRole;
	private BookContributorRegisterRequest registerRequest;
	private BookContributor bookContributor;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		book = Book.builder()
			.publisherId(new Publisher(1L, "Sample Publisher"))
			.state(BookStateEnum.NEW)
			.title("Sample Book")
			.thumbnailImageUrl("https://example.com/thumbnail.jpg")
			.detailImageUrl("https://example.com/detail.jpg")
			.publicationDate(LocalDate.now())
			.price(BigDecimal.valueOf(29.99))
			.discountRate(10)
			.description("Sample description.")
			.contents("Sample contents.")
			.isbn("978-3-16-148410-0")
			.isPackageable(true)
			.stock(100)
			.likeCount(0)
			.viewCount(0L)
			.build();

		TestUtils.setIdForEntity(book, 1L);

		contributor = Contributor.builder().id(1L).name("contributor").build();
		contributorRole = new ContributorRole(1L, ContributorRoleEnum.AUTHOR);

		bookContributor = BookContributor.builder()
			.book(book)
			.contributor(contributor)
			.build();

		TestUtils.setIdForEntity(bookContributor, 1L);

		registerRequest = new BookContributorRegisterRequest(1L, 1L, List.of(1L));

	}

	@DisplayName("도서 기여자 등록 성공")
	@Test
	void testRegisterContributorToBook_Success() {
		when(bookRepository.findById(registerRequest.bookId())).thenReturn(Optional.of(book));
		when(contributorRepository.findById(registerRequest.contributorId())).thenReturn(Optional.of(contributor));
		when(contributorRoleRepository.findById(1L)).thenReturn(Optional.of(contributorRole));

		bookContributorService.registerContributorToBook(registerRequest);

		verify(bookContributorRepository, times(1)).save(any(BookContributor.class));
	}

	@DisplayName("도서 기여자 등록 실패 - 책 not found")
	@Test
	void testRegisterContributorToBook_BookNotFound() {
		when(bookRepository.findById(registerRequest.bookId())).thenReturn(Optional.empty());

		assertThrows(BookNotFoundException.class,
			() -> bookContributorService.registerContributorToBook(registerRequest));
		verify(bookContributorRepository, never()).save(any(BookContributor.class));
	}

	@DisplayName("도서 기여자 등록 실패 - 기여자 not found")
	@Test
	void testRegisterContributorToBook_ContributorNotFound() {
		when(bookRepository.findById(registerRequest.bookId())).thenReturn(Optional.of(book));
		when(contributorRepository.findById(registerRequest.contributorId())).thenReturn(Optional.empty());

		assertThrows(ContributorNotFoundException.class,
			() -> bookContributorService.registerContributorToBook(registerRequest));
		verify(bookContributorRepository, never()).save(any(BookContributor.class));
	}

	@DisplayName("도서 기여자 등록 실패 - 기여자 역할 not found")
	@Test
	void testRegisterContributorToBook_ContributorRoleNotFound() {
		when(bookRepository.findById(registerRequest.bookId())).thenReturn(Optional.of(book));
		when(contributorRepository.findById(registerRequest.contributorId())).thenReturn(Optional.of(contributor));
		when(contributorRoleRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ContributorRoleNotFoundException.class,
			() -> bookContributorService.registerContributorToBook(registerRequest));
		verify(bookContributorRepository, never()).save(any(BookContributor.class));
	}

	@DisplayName("도서 기여자 조회 성공")
	@Test
	void getAllBooksByContributorId_ShouldReturnBooks_WhenContributorExists() {
		// Arrange
		when(contributorRepository.existsById(contributor.getId())).thenReturn(true);
		when(bookContributorRepository.findBookIdsByContributorId(contributor.getId())).thenReturn(
			List.of(book.getId()));
		when(bookRepository.findAllById(List.of(book.getId()))).thenReturn(List.of(book));
		// Act
		List<Book> result = bookContributorService.getAllBooksByContributorId(contributor.getId());

		// Assert
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(book.getTitle(), result.getFirst().getTitle());
	}

	@DisplayName("도서 ID로 기여자와 기여자 역할 조회 성공")
	@Test
	void getContributorsAndRolesByBookId_ShouldReturnContributors_WhenBookExists() {
		Long bookId = 1L;
		BookContributorInfoResponse contributorInfoResponse = BookContributorInfoResponse.of(
			1L,
			"contributor",
			1L,
			ContributorRoleEnum.AUTHOR.getKorName()
		);
		when(bookContributorRepository.findContributorsAndRolesByBookId(bookId))
			.thenReturn(List.of(contributorInfoResponse));

		// Act
		List<BookContributorInfoResponse> result = bookContributorService.getContributorsAndRolesByBookId(bookId);

		// Assert
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(contributorInfoResponse.contributorId(), result.getFirst().contributorId());
		assertEquals(contributorInfoResponse.contributorName(), result.getFirst().contributorName());
		assertEquals(contributorInfoResponse.contributorRoleId(), result.getFirst().contributorRoleId());
		assertEquals(contributorInfoResponse.contributorRoleName(), result.getFirst().contributorRoleName());

		verify(bookContributorRepository).findContributorsAndRolesByBookId(bookId);
	}

	@DisplayName("도서 기여자 조회 실패 - 기여자 not found")
	@Test
	void getAllBooksByContributorId_ShouldThrowContributorNotFoundException_WhenContributorDoesNotExist() {
		// Arrange
		when(contributorRepository.existsById(contributor.getId())).thenReturn(false);

		// Act & Assert
		assertThrows(ContributorNotFoundException.class,
			() -> bookContributorService.getAllBooksByContributorId(contributor.getId()));
		verify(contributorRepository).existsById(contributor.getId());
	}

	@DisplayName("도서 기여자 삭제 성공")
	@Test
	void deleteBookContributor_ShouldDeleteBookContributor_WhenExists() {
		// Arrange
		when(bookContributorRepository.findById(bookContributor.getId())).thenReturn(Optional.of(bookContributor));

		// Act
		bookContributorService.deleteBookContributor(bookContributor.getId());

		// Assert
		verify(bookContributorRepository).delete(bookContributor);
	}

	@DisplayName("도서 기여자 삭제 실패 - 도서 기여자 not found")

	@Test
	void deleteBookContributor_ShouldThrowBookContributorNotFoundException_WhenNotExists() {
		// Arrange
		when(bookContributorRepository.findById(bookContributor.getId())).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(BookContributorNotFoundException.class,
			() -> bookContributorService.deleteBookContributor(bookContributor.getId()));
		verify(bookContributorRepository).findById(bookContributor.getId());
	}
}