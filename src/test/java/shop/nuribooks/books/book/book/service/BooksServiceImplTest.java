package shop.nuribooks.books.book.book.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import shop.nuribooks.books.book.book.dto.AladinBookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BookContributorsResponse;
import shop.nuribooks.books.book.book.dto.BookListResponse;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.dto.BookUpdateRequest;
import shop.nuribooks.books.book.book.dto.PersonallyBookRegisterRequest;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.entity.BookStateEnum;
import shop.nuribooks.books.book.book.mapper.BookMapper;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.book.service.impl.BookServiceImpl;
import shop.nuribooks.books.book.book.utility.BookUtils;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;
import shop.nuribooks.books.book.bookcontributor.entity.BookContributor;
import shop.nuribooks.books.book.bookcontributor.repository.BookContributorRepository;
import shop.nuribooks.books.book.booktag.repository.BookTagRepository;
import shop.nuribooks.books.book.booktag.service.BookTagService;
import shop.nuribooks.books.book.category.dto.SimpleCategoryResponse;
import shop.nuribooks.books.book.category.entity.BookCategory;
import shop.nuribooks.books.book.category.entity.Category;
import shop.nuribooks.books.book.category.repository.BookCategoryRepository;
import shop.nuribooks.books.book.category.repository.CategoryRepository;
import shop.nuribooks.books.book.category.service.BookCategoryService;
import shop.nuribooks.books.book.contributor.entity.ContributorRole;
import shop.nuribooks.books.book.contributor.entity.ContributorRoleEnum;
import shop.nuribooks.books.book.contributor.repository.ContributorRepository;
import shop.nuribooks.books.book.contributor.repository.role.ContributorRoleRepository;
import shop.nuribooks.books.book.publisher.entity.Publisher;
import shop.nuribooks.books.book.publisher.repository.PublisherRepository;
import shop.nuribooks.books.exception.InvalidPageRequestException;
import shop.nuribooks.books.exception.book.BookIdNotFoundException;
import shop.nuribooks.books.exception.book.InvalidBookStateException;
import shop.nuribooks.books.exception.book.ResourceAlreadyExistIsbnException;
import shop.nuribooks.books.exception.contributor.InvalidContributorRoleException;

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
	private ContributorRepository contributorRepository;

	@Mock
	private ContributorRoleRepository contributorRoleRepository;

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
	private ContributorRole contributorRole;

	@BeforeEach
	public void setUp() {
		publisher = Publisher.builder()
			.name("누리북스")
			.build();

		contributorRole = ContributorRole.builder()
			.name(ContributorRoleEnum.AUTHOR)
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
			"소설 > 한국소설"
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
	@DisplayName("registerBook - AladinBookRegisterRequest를 통한 도서 등록 테스트")
	void registerBookWithAladin() {
		BookStateEnum.fromStringKor(aladinRegisterRequest.getState());

		when(bookRepository.existsByIsbn(aladinRegisterRequest.getIsbn())).thenReturn(false);
		when(publisherRepository.findByName(aladinRegisterRequest.getPublisherName()))
			.thenReturn(Optional.of(publisher));
		when(bookRepository.save(any(Book.class))).thenReturn(book);
		when(contributorRoleRepository.findByName(ContributorRoleEnum.AUTHOR))
			.thenReturn(Optional.of(contributorRole));
		when(categoryRepository.findByNameAndParentCategory(any(), any()))
			.thenReturn(Optional.empty());

		bookService.registerBook(aladinRegisterRequest);

		verify(bookRepository, times(1)).save(any(Book.class));
		verify(publisherRepository, times(1)).findByName(aladinRegisterRequest.getPublisherName());
		verify(contributorRoleRepository, times(1)).findByName(ContributorRoleEnum.AUTHOR);
		verify(bookContributorRepository, times(1)).save(any(BookContributor.class));
		verify(categoryRepository, times(2)).save(any(Category.class));
		verify(bookCategoryRepository, times(1)).save(any(BookCategory.class));
	}

	@Test
	@DisplayName("registerBook - personallyBookRegisterRequest를 통한 도서 등록 테스트")
	public void registerBookWithPersonally() {
		BookStateEnum.fromStringKor(personallyRegisterRequest.getState());

		when(bookRepository.existsByIsbn(personallyRegisterRequest.getIsbn())).thenReturn(false);
		when(publisherRepository.findByName(personallyRegisterRequest.getPublisherName()))
			.thenReturn(Optional.of(publisher));
		when(bookRepository.save(any(Book.class))).thenReturn(book);
		when(contributorRoleRepository.findByName(ContributorRoleEnum.AUTHOR))
			.thenReturn(Optional.of(contributorRole));
		when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(new Category()));

		bookService.registerBook(personallyRegisterRequest);

		verify(bookRepository, times(1)).save(any(Book.class));
		verify(publisherRepository, times(1)).findByName(personallyRegisterRequest.getPublisherName());
		verify(contributorRoleRepository, times(1)).findByName(ContributorRoleEnum.AUTHOR);
		verify(bookContributorRepository, times(1)).save(any(BookContributor.class));
		verify(bookCategoryRepository, times(2)).save(any(BookCategory.class));
	}

	@Test
	@DisplayName("registerBook - TagIds가 null일경우")
	public void registerBookWithTagIdsIsNull() {
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
			null,
			List.of(1L, 2L)
		);

		BookStateEnum.fromStringKor(personallyRegisterRequest.getState());

		when(bookRepository.existsByIsbn(personallyRegisterRequest.getIsbn())).thenReturn(false);
		when(publisherRepository.findByName(personallyRegisterRequest.getPublisherName()))
			.thenReturn(Optional.of(publisher));
		when(bookRepository.save(any(Book.class))).thenReturn(book);
		when(contributorRoleRepository.findByName(ContributorRoleEnum.AUTHOR))
			.thenReturn(Optional.of(contributorRole));
		when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(new Category()));

		bookService.registerBook(personallyRegisterRequest);

		verify(bookTagService, never()).registerTagToBook(anyLong(), any());
	}

	@Test
	@DisplayName("registerBook - TagIds가 비어 있는 경우 도서 등록 테스트")
	void registerBookWithEmptyTagIds() {
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
			Collections.emptyList(),
			List.of(1L, 2L)
		);

		BookStateEnum.fromStringKor(personallyRegisterRequest.getState());

		when(bookRepository.existsByIsbn(personallyRegisterRequest.getIsbn())).thenReturn(false);
		when(publisherRepository.findByName(personallyRegisterRequest.getPublisherName()))
			.thenReturn(Optional.of(publisher));
		when(bookRepository.save(any(Book.class))).thenReturn(book);
		when(contributorRoleRepository.findByName(ContributorRoleEnum.AUTHOR))
			.thenReturn(Optional.of(contributorRole));
		when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(new Category()));

		bookService.registerBook(personallyRegisterRequest);

		verify(bookTagService, never()).registerTagToBook(anyLong(), any());
	}

	@Test
	@DisplayName("ISBN 중복 시 ResourceAlreadyExistIsbnException 예외 발생")
	public void registerBookResourceAlreadyExistException() {
		when(bookRepository.existsByIsbn(aladinRegisterRequest.getIsbn())).thenReturn(true);

		assertThrows(ResourceAlreadyExistIsbnException.class, () -> bookService.registerBook(aladinRegisterRequest));

		verify(bookRepository, never()).save(any(Book.class));
	}

	@Test
	@DisplayName("책 등록 시 출판사 저장실패 예외 발생")
	public void registerBookPublisherException() {
		when(bookRepository.existsByIsbn(aladinRegisterRequest.getIsbn())).thenReturn(false);
		when(publisherRepository.findByName(aladinRegisterRequest.getPublisherName())).thenReturn(Optional.empty());
		when(publisherRepository.save(any(Publisher.class))).thenThrow(
			new RuntimeException("Error saving book entity"));

		assertThrows(RuntimeException.class, () -> bookService.registerBook(aladinRegisterRequest));
	}

	@Test
	@DisplayName("책 등록 시 도서상태 저장실패 예외 발생")
	public void registerBookStateInvalidException() {
		when(bookRepository.existsByIsbn(aladinRegisterRequest.getIsbn())).thenReturn(false);
		when(publisherRepository.findByName(aladinRegisterRequest.getPublisherName())).thenReturn(
			Optional.of(publisher));

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
	public void registerBookException() {
		when(bookRepository.existsByIsbn(aladinRegisterRequest.getIsbn())).thenReturn(false);
		when(publisherRepository.findByName(aladinRegisterRequest.getPublisherName())).thenReturn(
			Optional.of(publisher));
		when(bookRepository.save(any(Book.class))).thenThrow(new RuntimeException("Book save error"));

		assertThrows(RuntimeException.class, () -> bookService.registerBook(aladinRegisterRequest));
	}

	@Test
	@DisplayName("음수 페이지 번호로 책 조회 시 InvalidPageRequestException 예외 발생")
	public void getBooksInvalidException() {
		Pageable pageable = mock(Pageable.class);
		when(pageable.getPageNumber()).thenReturn(-1);

		InvalidPageRequestException exception = assertThrows(InvalidPageRequestException.class,
			() -> bookService.getBooks(pageable));
		assertEquals("조회 가능한 페이지 범위를 벗어났습니다.", exception.getMessage());
		verify(bookRepository, never()).findAllWithPublisher(any(Pageable.class));
	}

	// @Test
	// @DisplayName("getBooks - 유효한 페이지 요청 시 빈 리스트 반환")
	// public void getBooksReturnEmptyPage() {
	// 	Pageable pageable = mock(Pageable.class);
	// 	when(pageable.getPageNumber()).thenReturn(1);
	// 	when(pageable.getPageSize()).thenReturn(10);
	//
	// 	List<BookListResponse> list = List.of();
	// 	when(bookRepository.findAllWithPublisher(pageable)).thenReturn(list);
	//
	// 	Page<BookContributorsResponse> response = bookService.getBooks(pageable);
	//
	// 	assertNotNull(response);
	// 	assertTrue(response.getContent().isEmpty());
	// 	assertEquals(1, response.getNumber());
	// 	assertEquals(10, response.getSize());
	// 	assertEquals(1, response.getTotalPages());
	// 	assertEquals(0, response.getTotalElements());
	// }

	@Test
	@DisplayName("페이지 반환")
	public void getBooks() {
		Pageable pageable = mock(Pageable.class);

		Book book1 = Book.builder()
			.publisherId(publisher)
			.price(BigDecimal.valueOf(15000))
			.state(BookStateEnum.NORMAL)
			.thumbnailImageUrl("thumbnail1.jpg")
			.detailImageUrl("detail1.jpg")
			.title("Book Title 1")
			.build();

		ReflectionTestUtils.setField(book1, "id", 1L);

		Book book2 = Book.builder()
			.publisherId(publisher)
			.price(BigDecimal.valueOf(20000))
			.state(BookStateEnum.NORMAL)
			.thumbnailImageUrl("thumbnail2.jpg")
			.detailImageUrl("detail2.jpg")
			.title("Book Title 2")
			.build();

		ReflectionTestUtils.setField(book2, "id", 2L);

		List<BookListResponse> books = List.of(BookListResponse.of(book1), BookListResponse.of(book2));
		when(bookRepository.findAllWithPublisher(pageable)).thenReturn(books);

		List<BookContributorInfoResponse> contributors1 = List.of(
			new BookContributorInfoResponse(1L, "카트보이", 1L, "지은이")
		);

		List<BookContributorInfoResponse> contributors2 = List.of(
			new BookContributorInfoResponse(2L, "멤버보이", 1L, "지은이")
		);

		when(bookContributorRepository.findContributorsAndRolesByBookId(1L)).thenReturn(contributors1);
		when(bookContributorRepository.findContributorsAndRolesByBookId(2L)).thenReturn(contributors2);
		when(bookRepository.countBook()).thenReturn(2l);

		Map<String, List<String>> contributorsByRole1 = Map.of(
			"지은이", List.of("카트보이")
		);

		Map<String, List<String>> contributorsByRole2 = Map.of(
			"지은이", List.of("멤버보이")
		);

		try (MockedStatic<BookUtils> mockedUtils = mockStatic(BookUtils.class)) {
			mockedUtils.when(() -> BookUtils.groupContributorsByRole(contributors1)).thenReturn(contributorsByRole1);
			mockedUtils.when(() -> BookUtils.groupContributorsByRole(contributors2)).thenReturn(contributorsByRole2);

			Page<BookContributorsResponse> response = bookService.getBooks(pageable);

			assertNotNull(response);
			assertEquals(2, response.getContent().size());
			assertEquals(0, response.getNumber());
			assertEquals(2, response.getSize());
			assertEquals(1, response.getTotalPages());
			assertEquals(2, response.getTotalElements());

			BookContributorsResponse book1Response = response.getContent().getFirst();
			assertEquals("Book Title 1", book1Response.bookDetails().title());
			assertEquals(contributorsByRole1, book1Response.contributorsByRole());

			BookContributorsResponse book2Response = response.getContent().get(1);
			assertEquals("Book Title 2", book2Response.bookDetails().title());
			assertEquals(contributorsByRole2, book2Response.contributorsByRole());
		}
	}

	@Test
	@DisplayName("존재하지 않는 책 ID로 책 업데이트 시 BookIdNotFoundException 예외 발생")
	public void updateBookNotFoundException() {
		when(bookRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(BookIdNotFoundException.class, () -> bookService.updateBook(1L, updateRequest));
	}

	@Test
	@DisplayName("유효한 책 ID로 책 업데이트 성공")
	public void updateBookComplete() {
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
	public void getBookByIdComplete() {
		when(bookRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(book));

		List<BookContributorInfoResponse> contributorResponses = List.of(
			new BookContributorInfoResponse(1L, "이정규", 1L, "지은이"),
			new BookContributorInfoResponse(1L, "임건우", 2L, "엮은이")
		);
		lenient().when(bookContributorRepository.findContributorsAndRolesByBookId(book.getId()))
			.thenReturn(contributorResponses);

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

			BookResponse result = bookService.getBookById(1L, false);

			assertNotNull(result, "존재하지 않는 도서입니다.");
			assertEquals(bookResponse, result);
		}
	}

	@Test
	@DisplayName("유효한 책 ID로 책 조회")
	public void getBookByIdCompleteAndUpdateCount() {
		when(bookRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(book));

		List<BookContributorInfoResponse> contributorResponses = List.of(
			new BookContributorInfoResponse(1L, "이정규", 1L, "지은이"),
			new BookContributorInfoResponse(1L, "임건우", 2L, "엮은이")
		);
		lenient().when(bookContributorRepository.findContributorsAndRolesByBookId(book.getId()))
			.thenReturn(contributorResponses);

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

			BookResponse result = bookService.getBookById(1L, true);

			assertNotNull(result, "존재하지 않는 도서입니다.");
			assertEquals(bookResponse, result);
		}
	}

	@Test
	@DisplayName("유효한 책 ID로 책 삭제 성공")
	public void deleteBookComplete() {
		when(bookRepository.findBookByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(book));

		bookService.deleteBook(1L);

		verify(bookRepository, times(1)).findBookByIdAndDeletedAtIsNull(1L);
	}

	@Test
	@DisplayName("도서id가 null일 경우 삭제 시 예외발생")
	public void deleteBookException() {
		assertThrows(BookIdNotFoundException.class, () -> bookService.deleteBook(null));
		verify(bookRepository, never()).findBookByIdAndDeletedAtIsNull(anyLong());
	}

	@Test
	@DisplayName("registerAladinCategories 카테고리 저장")
	void registerAladinCategories() {
		when(bookRepository.save(any(Book.class))).thenReturn(book);
		when(categoryRepository.findByNameAndParentCategory(anyString(), any()))
			.thenReturn(Optional.empty());

		bookService.registerBook(aladinRegisterRequest);

		verify(categoryRepository, times(2)).save(any(Category.class));
		verify(bookCategoryRepository, times(1)).save(any(BookCategory.class));
	}

	@Test
	@DisplayName("parseContributors - 작가-역할 파싱")
	public void parseContributors() {
		String authorRole = "카트가이, 포장지가이 (지은이), 멤버가이 (옮긴이)";

		List<BookServiceImpl.ParsedContributor> parsedContributors = ReflectionTestUtils.invokeMethod(
			bookService, "parseContributors", authorRole
		);

		assertNotNull(parsedContributors);
		assertEquals(3, parsedContributors.size());

		assertEquals("카트가이", parsedContributors.get(0).getName());
		assertEquals("지은이", parsedContributors.get(0).getRole());

		assertEquals("포장지가이", parsedContributors.get(1).getName());
		assertEquals("지은이", parsedContributors.get(1).getRole());

		assertEquals("멤버가이", parsedContributors.get(2).getName());
		assertEquals("옮긴이", parsedContributors.get(2).getRole());
	}

	@Test
	@DisplayName("parseContributors - 작가-역할 파싱 입력 시 괄호 안닫힘 예외처리")
	public void parseContributorsNotClosedParenthesisIndex() {
		String authorRole = "카트가이 (지은이) , 포장지가이 (옮긴이";

		InvalidContributorRoleException exception = assertThrows(InvalidContributorRoleException.class, () -> {
			ReflectionTestUtils.invokeMethod(bookService, "parseContributors", authorRole);
		});

		assertEquals("역할 이름이 괄호로 닫히지 않아 작가-역할 저장에 실패했습니다.", exception.getMessage());
	}

	@Test
	@DisplayName("parseContributors - 작가-역할 파싱 역할 없음 예외처리")
	public void parseContributorsExceptionWithoutRole() {
		String authorRole = "카트가이 (지은이), 포장지가이";

		InvalidContributorRoleException exception = assertThrows(InvalidContributorRoleException.class, () -> {
			ReflectionTestUtils.invokeMethod(bookService, "parseContributors", authorRole);
		});

		assertEquals("입력 마지막에 괄호로 역할이 지정되지 않아 작가-역할 저장에 실패합니다.", exception.getMessage());
	}
}