package shop.nuribooks.books.book.book.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import shop.nuribooks.books.book.book.dto.AladinBookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.dto.BookUpdateRequest;
import shop.nuribooks.books.book.book.dto.PersonallyBookRegisterRequest;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.entity.BookStateEnum;
import shop.nuribooks.books.book.book.mapper.BookMapper;
import shop.nuribooks.books.book.book.utility.BookUtils;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;
import shop.nuribooks.books.book.bookcontributor.repository.BookContributorRepository;
import shop.nuribooks.books.book.booktag.repository.BookTagRepository;
import shop.nuribooks.books.book.booktag.service.BookTagService;
import shop.nuribooks.books.book.category.dto.SimpleCategoryResponse;
import shop.nuribooks.books.book.category.entity.Category;
import shop.nuribooks.books.book.category.repository.BookCategoryRepository;
import shop.nuribooks.books.book.category.repository.CategoryRepository;
import shop.nuribooks.books.book.category.service.BookCategoryService;
import shop.nuribooks.books.book.publisher.entity.Publisher;
import shop.nuribooks.books.exception.InvalidPageRequestException;
import shop.nuribooks.books.exception.book.BookIdNotFoundException;
import shop.nuribooks.books.exception.book.InvalidBookStateException;
import shop.nuribooks.books.exception.book.ResourceAlreadyExistIsbnException;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.publisher.repository.PublisherRepository;
import shop.nuribooks.books.book.book.service.impl.BookServiceImpl;

@ExtendWith(MockitoExtension.class)
public class BooksServiceImplTest {

	@InjectMocks
	private BookServiceImpl bookService;

	@Mock
	private BookMapper bookMapper;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private PublisherRepository publisherRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private BookContributorRepository bookContributorRepository;

	@Mock
	private BookTagRepository bookTagRepository;

	@Mock
	private BookCategoryRepository bookCategoryRepository;

	@Mock
	private BookTagService bookTagService;

	@Mock
	private BookCategoryService bookCategoryService;

	private AladinBookRegisterRequest aladinRegisterRequest;
	private PersonallyBookRegisterRequest personallyRegisterRequest;
	private BookUpdateRequest updateRequest;
	private Book book;
	private Publisher publisher;

	@BeforeEach
	public void setUp() {
		publisher = Publisher.builder()
			.name("누리북스")
			.build();

		ReflectionTestUtils.setField(publisher, "id", 1L);

		aladinRegisterRequest = new AladinBookRegisterRequest(
			"Sample Book Title",
			"이정규 (지은이)",
			"누리북스",
			LocalDate.of(2022, 1, 1),
			BigDecimal.valueOf(20000),
			10,
			100,
			BookStateEnum.NORMAL.getKorName(),
			"thumbnail.jpg",
			"detail.jpg",
			"Sample description",
			"Sample contents",
			"1234567890123",
			true,
			List.of(1L, 2L),
			"Category Name"
		);

		personallyRegisterRequest = new PersonallyBookRegisterRequest(
			"Sample Book Title",
			"이정규 (지은이)",
			"누리북스",
			LocalDate.of(2022, 1, 1),
			BigDecimal.valueOf(20000),
			10,
			100,
			BookStateEnum.NORMAL.getKorName(),
			"thumbnail.jpg",
			"detail.jpg",
			"Sample description",
			"Sample contents",
			"1234567890123",
			true,
			List.of(1L, 2L),
			List.of(1L, 2L)
		);

		updateRequest = new BookUpdateRequest(
			BigDecimal.valueOf(25000),
			15,
			50,
			BookStateEnum.NEW.getKorName(),
			"updated_thumbnail.jpg",
			"updated_detail.jpg",
			"Updated Description",
			"Updated Contents",
			true,
			List.of(1L, 2L, 3L),
			List.of(1L, 2L)
		);

		book = Book.builder()
			.publisherId(publisher)
			.price(BigDecimal.valueOf(20000))
			.discountRate(10)
			.stock(100)
			.state(BookStateEnum.NORMAL)
			.thumbnailImageUrl("original_thumbnail.jpg")
			.detailImageUrl("original_detail.jpg")
			.description("Original Description")
			.contents("Original Contents")
			.isPackageable(true)
			.likeCount(0)
			.viewCount(0L)
			.build();

		ReflectionTestUtils.setField(book, "id", 1L);
	}

	@Test
	@DisplayName("ISBN 중복 시 ResourceAlreadyExistIsbnException 예외 발생")
	public void registerBook_ShouldThrowResourceAlreadyExistIsbnException_WhenBookWithIsbnExists() {
		when(bookRepository.existsByIsbn(aladinRegisterRequest.getIsbn())).thenReturn(true);

		assertThrows(ResourceAlreadyExistIsbnException.class, () -> bookService.registerBook(aladinRegisterRequest));
	}

	@Test
	@DisplayName("책 등록 시 출판사 저장실패 예외 발생")
	public void registerBook_ShouldThrowException_WhenPublisherSaveFail() {
		when(bookRepository.existsByIsbn(aladinRegisterRequest.getIsbn())).thenReturn(false);
		when(publisherRepository.findByName(aladinRegisterRequest.getPublisherName())).thenReturn(Optional.empty());
		when(publisherRepository.save(any(Publisher.class))).thenThrow(new RuntimeException("Error saving book entity"));

		assertThrows(RuntimeException.class, () -> bookService.registerBook(aladinRegisterRequest));
	}

	@Test
	@DisplayName("책 등록 시 도서상태 저장실패 예외 발생")
	public void registerBook_ShouldThrowIllegalException_WhenStateSaveFail() {
		when(bookRepository.existsByIsbn(aladinRegisterRequest.getIsbn())).thenReturn(false);
		when(publisherRepository.findByName(aladinRegisterRequest.getPublisherName())).thenReturn(Optional.of(publisher));

		AladinBookRegisterRequest invalidStateRequest = new AladinBookRegisterRequest(
			"Sample Book Title",
			"이정규 (지은이)",
			"누리북스",
			LocalDate.of(2022, 1, 1),
			BigDecimal.valueOf(20000),
			10,
			100,
			"실패",
			"thumbnail.jpg",
			"detail.jpg",
			"Sample description",
			"Sample contents",
			"1234567890123",
			true,
			List.of(1L, 2L),
			"Category Name"
		);

		assertThrows(InvalidBookStateException.class, () -> bookService.registerBook(invalidStateRequest));
	}

	@Test
	@DisplayName("책 등록 시 도서 저장 실패 예외")
	public void registerBook_ShouldThrowException_WhenBookSaveFail() {
		when(bookRepository.existsByIsbn(aladinRegisterRequest.getIsbn())).thenReturn(false);
		when(publisherRepository.findByName(aladinRegisterRequest.getPublisherName())).thenReturn(Optional.of(publisher));
		when(bookRepository.save(any(Book.class))).thenThrow(new RuntimeException("Book save error"));

		assertThrows(RuntimeException.class, () -> bookService.registerBook(aladinRegisterRequest));
	}

	@Test
	@DisplayName("유효한 Aladin 요청 시 책 저장")
	public void registerBook_ShouldSaveBook_WhenValidAladinRequest() {
		when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
			Book savedBook = invocation.getArgument(0);
			ReflectionTestUtils.setField(savedBook, "id", 1L);
			return savedBook;
		});

		when(bookRepository.existsByIsbn(aladinRegisterRequest.getIsbn())).thenReturn(false);
		when(publisherRepository.findByName(aladinRegisterRequest.getPublisherName())).thenReturn(Optional.of(publisher));

		bookService.registerBook(aladinRegisterRequest);

		verify(bookRepository, times(1)).save(any(Book.class));
		verify(bookTagService, times(1)).registerTagToBook(eq(1L), eq(aladinRegisterRequest.getTagIds()));
	}

	@Test
	@DisplayName("음수 페이지 번호로 책 조회 시 InvalidPageRequestException 예외 발생")
	public void getBooks_ShouldThrowInvalidPageRequestException_WhenPageNumberIsNegative() {
		Pageable pageable = mock(Pageable.class);
		when(pageable.getPageNumber()).thenReturn(-1);

		InvalidPageRequestException exception = assertThrows(InvalidPageRequestException.class, () -> bookService.getBooks(pageable));
		assertEquals("페이지 번호는 0 이상이어야 합니다.", exception.getMessage());
	}

	@Test
	@DisplayName("존재하지 않는 책 ID로 책 업데이트 시 BookIdNotFoundException 예외 발생")
	public void updateBook_ShouldThrowBookIdNotFoundException_WhenBookNotFound() {
		when(bookRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(BookIdNotFoundException.class, () -> bookService.updateBook(1L, updateRequest));
	}

	@Test
	@DisplayName("유효한 책 ID로 책 업데이트 성공")
	public void updateBook_ShouldUpdateBook_WhenBookExists() {
		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(categoryRepository.findById(any())).thenReturn(Optional.of(new Category()));

		bookService.updateBook(1L, updateRequest);

		assertEquals(updateRequest.price(), book.getPrice());
		assertEquals(updateRequest.discountRate(), book.getDiscountRate());
		assertEquals(updateRequest.stock(), book.getStock());
		assertEquals(updateRequest.state(), book.getState().getKorName());
		assertEquals(updateRequest.thumbnailImageUrl(), book.getThumbnailImageUrl());
		assertEquals(updateRequest.detailImageUrl(), book.getDetailImageUrl());
		assertEquals(updateRequest.description(), book.getDescription());
		assertEquals(updateRequest.contents(), book.getContents());
		assertEquals(updateRequest.isPackageable(), book.isPackageable());

		verify(bookTagService, times(1)).deleteBookTagIds(1L);
		verify(bookTagService, times(1)).registerTagToBook(1L, updateRequest.tagIds());
		verify(bookCategoryService, times(1)).deleteBookCategories(1L);
	}

	@Test
	@DisplayName("유효한 책 ID로 책 조회")
	public void getBookById_ShouldReturnBook_WhenBookExists() {
		when(bookRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(book));

		List<BookContributorInfoResponse> contributorResponses = List.of(
			new BookContributorInfoResponse(1L, "이정규", 1L, "지은이"),
			new BookContributorInfoResponse(1L, "임건우", 2L, "엮은이")
		);
		lenient().when(bookContributorRepository.findContributorsAndRolesByBookId(book.getId())).thenReturn(contributorResponses);

		try (MockedStatic<BookUtils> mockedStatic = mockStatic(BookUtils.class)) {
			Map<String, List<String>> contributorsByRole = Map.of(
				"지은이", List.of("이정규"),
				"엮은이", List.of("임건우")
			);
			mockedStatic.when(() -> BookUtils.groupContributorsByRole(contributorResponses))
				.thenReturn(contributorsByRole);

			List<String> tagNames = List.of("wow", "amazing");
			lenient().when(bookTagRepository.findTagNamesByBookId(book.getId())).thenReturn(tagNames);

			List<List<SimpleCategoryResponse>> categories = List.of(
				List.of(new SimpleCategoryResponse(1L, "국내도서"),
					new SimpleCategoryResponse(2L, "문학"))
			);
			lenient().when(bookCategoryRepository.findCategoriesByBookId(book.getId())).thenReturn(categories);

			BigDecimal salePrice = BookUtils.calculateSalePrice(book.getPrice(), book.getDiscountRate());
			BookResponse bookResponse = new BookResponse(
				book.getId(),
				book.getPublisherId().getName(),
				book.getState().getKorName(),
				book.getTitle(),
				book.getThumbnailImageUrl(),
				book.getDetailImageUrl(),
				book.getPublicationDate(),
				book.getPrice(),
				book.getDiscountRate(),
				salePrice,
				book.getDescription(),
				book.getContents(),
				book.getIsbn(),
				book.isPackageable(),
				book.getLikeCount(),
				book.getStock(),
				book.getViewCount(),
				tagNames,
				contributorsByRole,
				categories
			);
			when(bookMapper.toBookResponse(book)).thenReturn(bookResponse);

			BookResponse result = bookService.getBookById(1L);

			assertNotNull(result, "존재하지 않는 도서입니다.");
			assertEquals(bookResponse, result);
		}
	}

	@Test
	@DisplayName("유효한 책 ID로 책 삭제 성공")
	public void deleteBook_ShouldDeleteBook_WhenBookExists() {
		when(bookRepository.findBookByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(book));

		bookService.deleteBook(1L);

		verify(bookRepository, times(1)).findBookByIdAndDeletedAtIsNull(1L);
	}

	@Test
	@DisplayName("도서id가 null일 경우 삭제 시 예외발생")
	public void deleteBook_ShouldThrown_WhenBookIdIsNull() {
		assertThrows(BookIdNotFoundException.class, () -> bookService.deleteBook(null));
		verify(bookRepository, never()).findBookByIdAndDeletedAtIsNull(anyLong());
	}

}