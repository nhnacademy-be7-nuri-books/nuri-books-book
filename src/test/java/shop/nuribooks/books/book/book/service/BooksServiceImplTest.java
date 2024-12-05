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

import shop.nuribooks.books.book.book.dto.request.AladinBookRegisterRequest;
import shop.nuribooks.books.book.book.dto.request.BaseBookRegisterRequest;
import shop.nuribooks.books.book.book.dto.request.BookUpdateRequest;
import shop.nuribooks.books.book.book.dto.request.PersonallyBookRegisterRequest;
import shop.nuribooks.books.book.book.dto.response.BookContributorsResponse;
import shop.nuribooks.books.book.book.dto.response.BookListResponse;
import shop.nuribooks.books.book.book.dto.response.BookResponse;
import shop.nuribooks.books.book.book.dto.response.TopBookResponse;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.entity.BookStateEnum;
import shop.nuribooks.books.book.book.mapper.BookMapper;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.book.service.impl.BookServiceImpl;
import shop.nuribooks.books.book.book.strategy.BookRegisterStrategy;
import shop.nuribooks.books.book.book.strategy.BookRegisterStrategyProvider;
import shop.nuribooks.books.book.book.utility.BookUtils;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;
import shop.nuribooks.books.book.bookcontributor.entity.BookContributor;
import shop.nuribooks.books.book.bookcontributor.repository.BookContributorRepository;
import shop.nuribooks.books.book.booktag.repository.BookTagRepository;
import shop.nuribooks.books.book.booktag.service.BookTagService;
import shop.nuribooks.books.book.category.dto.SimpleCategoryResponse;
import shop.nuribooks.books.book.category.repository.BookCategoryRepository;
import shop.nuribooks.books.book.category.service.BookCategoryService;
import shop.nuribooks.books.book.contributor.entity.Contributor;
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
class BooksServiceImplTest {

	@InjectMocks
	private BookServiceImpl bookService;

	@Mock
	private BookMapper bookMapper;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private PublisherRepository publisherRepository;

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

	@Mock
	private BookRegisterStrategyProvider bookRegisterStrategyProvider;

	@Mock
	private CategoryRegisterService categoryRegisterService;

	private AladinBookRegisterRequest aladinRegisterRequest;
	private PersonallyBookRegisterRequest personallyRegisterRequest;
	private BookUpdateRequest updateRequest;
	private Book book;
	private Publisher publisher;
	private ContributorRole contributorRole;

	@BeforeEach
	void setUp() {
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

		BookRegisterStrategy mockStrategy = mock(BookRegisterStrategy.class);
		when(bookRegisterStrategyProvider.getStrategy(any(BaseBookRegisterRequest.class)))
			.thenReturn(mockStrategy);
		doNothing().when(mockStrategy).registerCategory(any(BaseBookRegisterRequest.class), any(Book.class));

		bookService.registerBook(aladinRegisterRequest);

		verify(bookRepository, times(1)).save(any(Book.class));
		verify(publisherRepository, times(1)).findByName(aladinRegisterRequest.getPublisherName());
		verify(contributorRoleRepository, times(1)).findByName(ContributorRoleEnum.AUTHOR);
		verify(bookContributorRepository, times(1)).save(any(BookContributor.class));
		verify(mockStrategy, times(1)).registerCategory(any(BaseBookRegisterRequest.class), any(Book.class));
	}

	@Test
	@DisplayName("registerBook - personallyBookRegisterRequest를 통한 도서 등록 테스트")
	void registerBookWithPersonally() {
		BookStateEnum.fromStringKor(personallyRegisterRequest.getState());

		when(bookRepository.existsByIsbn(personallyRegisterRequest.getIsbn())).thenReturn(false);
		when(publisherRepository.findByName(personallyRegisterRequest.getPublisherName()))
			.thenReturn(Optional.of(publisher));
		when(bookRepository.save(any(Book.class))).thenReturn(book);
		when(contributorRoleRepository.findByName(ContributorRoleEnum.AUTHOR))
			.thenReturn(Optional.of(contributorRole));

		BookRegisterStrategy mockStrategy = mock(BookRegisterStrategy.class);
		when(bookRegisterStrategyProvider.getStrategy(personallyRegisterRequest))
			.thenReturn(mockStrategy);
		doNothing().when(mockStrategy).registerCategory(personallyRegisterRequest, book);

		bookService.registerBook(personallyRegisterRequest);

		verify(bookRepository, times(1)).save(any(Book.class));
		verify(publisherRepository, times(1)).findByName(personallyRegisterRequest.getPublisherName());
		verify(contributorRoleRepository, times(1)).findByName(ContributorRoleEnum.AUTHOR);
		verify(bookContributorRepository, times(1)).save(any(BookContributor.class));
		verify(mockStrategy, times(1)).registerCategory(any(BaseBookRegisterRequest.class), any(Book.class));
	}

	@Test
	@DisplayName("registerBook - TagIds가 null일경우")
	void registerBookWithTagIdsIsNull() {
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

		BookRegisterStrategy mockStrategy = mock(BookRegisterStrategy.class);
		when(bookRegisterStrategyProvider.getStrategy(any(BaseBookRegisterRequest.class)))
			.thenReturn(mockStrategy);
		doNothing().when(mockStrategy).registerCategory(any(BaseBookRegisterRequest.class), any(Book.class));

		bookService.registerBook(personallyRegisterRequest);

		verify(bookRepository, times(1)).save(any(Book.class));
		verify(publisherRepository, times(1)).findByName(personallyRegisterRequest.getPublisherName());
		verify(contributorRoleRepository, times(1)).findByName(ContributorRoleEnum.AUTHOR);
		verify(bookContributorRepository, times(1)).save(any(BookContributor.class));
		verify(bookTagService, never()).registerTagToBook(anyLong(), any());
		verify(mockStrategy, times(1)).registerCategory(any(BaseBookRegisterRequest.class), any(Book.class));
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

		BookRegisterStrategy mockStrategy = mock(BookRegisterStrategy.class);
		when(bookRegisterStrategyProvider.getStrategy(personallyRegisterRequest))
			.thenReturn(mockStrategy);

		doNothing().when(mockStrategy).registerCategory(personallyRegisterRequest, book);

		bookService.registerBook(personallyRegisterRequest);

		verify(bookRepository, times(1)).save(any(Book.class));
		verify(publisherRepository, times(1)).findByName(personallyRegisterRequest.getPublisherName());
		verify(contributorRoleRepository, times(1)).findByName(ContributorRoleEnum.AUTHOR);
		verify(bookContributorRepository, times(1)).save(any(BookContributor.class));
		verify(bookTagService, never()).registerTagToBook(anyLong(), any());
		verify(mockStrategy, times(1)).registerCategory(any(BaseBookRegisterRequest.class), any(Book.class));
	}

	@Test
	@DisplayName("registerBook - Contributor 및 ContributorRole이 존재하지 않을 경우 저장")
	void registerBookWithNewContributorAndRole() {
		BookStateEnum.fromStringKor(personallyRegisterRequest.getState());

		when(bookRepository.existsByIsbn(personallyRegisterRequest.getIsbn())).thenReturn(false);
		when(publisherRepository.findByName(personallyRegisterRequest.getPublisherName()))
			.thenReturn(Optional.of(publisher));
		when(bookRepository.save(any(Book.class))).thenReturn(book);

		when(contributorRepository.findByName("이정규")).thenReturn(Optional.empty());
		when(contributorRepository.save(any(Contributor.class))).thenAnswer(invocation -> {
			Contributor contributor = invocation.getArgument(0);
			ReflectionTestUtils.setField(contributor, "id", 1L);
			return contributor;
		});

		when(contributorRoleRepository.findByName(ContributorRoleEnum.AUTHOR))
			.thenReturn(Optional.empty());
		when(contributorRoleRepository.save(any(ContributorRole.class))).thenAnswer(invocation -> {
			ContributorRole role = invocation.getArgument(0);
			ReflectionTestUtils.setField(role, "id", 1L);
			return role;
		});

		BookRegisterStrategy mockStrategy = mock(BookRegisterStrategy.class);
		when(bookRegisterStrategyProvider.getStrategy(any(BaseBookRegisterRequest.class)))
			.thenReturn(mockStrategy);
		doNothing().when(mockStrategy).registerCategory(any(BaseBookRegisterRequest.class), any(Book.class));

		bookService.registerBook(personallyRegisterRequest);

		verify(bookRepository, times(1)).save(any(Book.class));
		verify(publisherRepository, times(1)).findByName(personallyRegisterRequest.getPublisherName());
		verify(contributorRoleRepository, times(1)).findByName(ContributorRoleEnum.AUTHOR);
		verify(contributorRepository, times(1)).findByName("이정규");
		verify(contributorRepository, times(1)).save(any(Contributor.class));
		verify(contributorRoleRepository, times(1)).save(any(ContributorRole.class));
		verify(bookContributorRepository, times(1)).save(any(BookContributor.class));
		verify(mockStrategy, times(1)).registerCategory(any(BaseBookRegisterRequest.class), any(Book.class));
	}

	@Test
	@DisplayName("ISBN 중복 시 ResourceAlreadyExistIsbnException 예외 발생")
	void registerBookResourceAlreadyExistException() {
		when(bookRepository.existsByIsbn(aladinRegisterRequest.getIsbn())).thenReturn(true);

		assertThrows(ResourceAlreadyExistIsbnException.class, () -> bookService.registerBook(aladinRegisterRequest));

		verify(bookRepository, never()).save(any(Book.class));
	}

	@Test
	@DisplayName("책 등록 시 출판사 저장실패 예외 발생")
	void registerBookPublisherException() {
		when(bookRepository.existsByIsbn(aladinRegisterRequest.getIsbn())).thenReturn(false);
		when(publisherRepository.findByName(aladinRegisterRequest.getPublisherName())).thenReturn(Optional.empty());
		when(publisherRepository.save(any(Publisher.class))).thenThrow(
			new RuntimeException("Error saving book entity"));

		assertThrows(RuntimeException.class, () -> bookService.registerBook(aladinRegisterRequest));
	}

	@Test
	@DisplayName("책 등록 시 도서상태 저장실패 예외 발생")
	void registerBookStateInvalidException() {
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
	void registerBookException() {
		when(bookRepository.existsByIsbn(aladinRegisterRequest.getIsbn())).thenReturn(false);
		when(publisherRepository.findByName(aladinRegisterRequest.getPublisherName())).thenReturn(
			Optional.of(publisher));
		when(bookRepository.save(any(Book.class))).thenThrow(new RuntimeException("Book save error"));

		assertThrows(RuntimeException.class, () -> bookService.registerBook(aladinRegisterRequest));
	}

	@Test
	@DisplayName("음수 페이지 번호로 책 조회 시 InvalidPageRequestException 예외 발생")
	void getBooksInvalidException() {
		Pageable pageable = mock(Pageable.class);
		when(pageable.getPageNumber()).thenReturn(-1);

		InvalidPageRequestException exception = assertThrows(InvalidPageRequestException.class,
			() -> bookService.getBooks(pageable));
		assertEquals("조회 가능한 페이지 범위를 벗어났습니다.", exception.getMessage());
		verify(bookRepository, never()).findAllWithPublisher(any(Pageable.class));
	}

	@Test
	@DisplayName("페이지 반환")
	void getBooks() {
		Pageable pageable = mock(Pageable.class);

		Book book1 = Book.builder()
			.publisherId(publisher)
			.price(BigDecimal.valueOf(15000))
			.state(BookStateEnum.NORMAL)
			.thumbnailImageUrl("thumbnail1.jpg")
			.title("Book Title 1")
			.build();

		ReflectionTestUtils.setField(book1, "id", 1L);

		Book book2 = Book.builder()
			.publisherId(publisher)
			.price(BigDecimal.valueOf(20000))
			.state(BookStateEnum.NORMAL)
			.thumbnailImageUrl("thumbnail2.jpg")
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
		when(bookRepository.countBook()).thenReturn(2L);

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
	void updateBookNotFoundException() {
		when(bookRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(BookIdNotFoundException.class, () -> bookService.updateBook(1L, updateRequest));
	}

	@Test
	@DisplayName("유효한 책 ID로 책 업데이트 성공")
	void updateBookComplete() {
		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

		doNothing().when(categoryRegisterService)
			.registerPersonallyCategories(updateRequest.categoryIds(), book);

		bookService.updateBook(1L, updateRequest);

		assertEquals(updateRequest.price(), book.getPrice());
		assertEquals(updateRequest.discountRate(), book.getDiscountRate());
		assertEquals(updateRequest.stock(), book.getStock());
		assertEquals(updateRequest.state(), book.getState().getKorName());
		assertEquals(updateRequest.thumbnailImageUrl(), book.getThumbnailImageUrl());
		assertEquals(updateRequest.description(), book.getDescription());
		assertEquals(updateRequest.contents(), book.getContents());
		assertEquals(updateRequest.isPackageable(), book.isPackageable());

		verify(bookTagService, times(1)).deleteBookTagIds(1L);
		verify(bookTagService, times(1)).registerTagToBook(1L, updateRequest.tagIds());
		verify(bookCategoryService, times(1)).deleteBookCategories(1L);
		verify(categoryRegisterService, times(1)).registerPersonallyCategories(anyList(), any(Book.class));
	}

	@Test
	@DisplayName("유효한 책 ID로 책 조회")
	void getBookByIdComplete() {
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
	@DisplayName("유효한 책 ID로 책 조회 및 조회수 업데이트")
	void getBookByIdCompleteAndUpdateCount() {
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
			assertEquals(1L, book.getViewCount());
		}
	}

	@Test
	@DisplayName("유효한 책 ID로 책 삭제 성공")
	void deleteBookComplete() {
		when(bookRepository.findBookByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(book));

		bookService.deleteBook(1L);

		verify(bookRepository, times(1)).findBookByIdAndDeletedAtIsNull(1L);
	}

	@Test
	@DisplayName("도서id가 null일 경우 삭제 시 예외발생")
	void deleteBookException() {
		assertThrows(BookIdNotFoundException.class, () -> bookService.deleteBook(null));
		verify(bookRepository, never()).findBookByIdAndDeletedAtIsNull(anyLong());
	}

	@Test
	@DisplayName("parseContributors - 작가-역할 파싱")
	void parseContributors() {
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
	void parseContributorsNotClosedParenthesisIndex() {
		String authorRole = "카트가이 (지은이) , 포장지가이 (옮긴이";

		InvalidContributorRoleException exception = assertThrows(InvalidContributorRoleException.class, () -> {
			ReflectionTestUtils.invokeMethod(bookService, "parseContributors", authorRole);
		});

		assertEquals("역할 이름이 괄호로 닫히지 않아 작가-역할 저장에 실패했습니다.", exception.getMessage());
	}

	@Test
	@DisplayName("parseContributors - 작가-역할 파싱 역할 없음 예외처리")
	void parseContributorsExceptionWithoutRole() {
		String authorRole = "카트가이 (지은이), 포장지가이";

		InvalidContributorRoleException exception = assertThrows(InvalidContributorRoleException.class, () -> {
			ReflectionTestUtils.invokeMethod(bookService, "parseContributors", authorRole);
		});

		assertEquals("입력 마지막에 괄호로 역할이 지정되지 않아 작가-역할 저장에 실패합니다.", exception.getMessage());
	}

	@Test
	@DisplayName("getTopBookLikes - Top books by likes 반환")
	void getTopBookLikesNotNull() {
		List<TopBookResponse> topLikes = List.of(
			new TopBookResponse(1L, "thumbnail1.jpg", "Book 1"),
			new TopBookResponse(2L, "thumbnail2.jpg", "Book 2")
		);
		when(bookRepository.findTopBooksByLikes()).thenReturn(topLikes);

		List<TopBookResponse> result = bookService.getTopBookLikes();

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(1L, result.getFirst().bookId());
		assertEquals("thumbnail1.jpg", result.get(0).thumbnailUrl());
		assertEquals("Book 1", result.get(0).title());

		assertEquals(2L, result.get(1).bookId());
		assertEquals("thumbnail2.jpg", result.get(1).thumbnailUrl());
		assertEquals("Book 2", result.get(1).title());

		verify(bookRepository, times(1)).findTopBooksByLikes();
	}

	@Test
	@DisplayName("getTopBookLikes - 좋아요가 null일 경우 빈 리스트 반환")
	void getTopBookLikesNull() {
		when(bookRepository.findTopBooksByLikes()).thenReturn(null);

		List<TopBookResponse> responses = bookService.getTopBookLikes();

		assertNotNull(responses);
		assertTrue(responses.isEmpty());

		verify(bookRepository, times(1)).findTopBooksByLikes();
	}

	@Test
	@DisplayName("getTopBookScores - Top books by scores 반환")
	void getTopBookScoresNotNull() {
		List<TopBookResponse> topScores = List.of(
			new TopBookResponse(1L, "thumbnail1.jpg", "Book 1"),
			new TopBookResponse(2L, "thumbnail2.jpg", "Book 2")
		);
		when(bookRepository.findTopBooksByScore()).thenReturn(topScores);

		List<TopBookResponse> result = bookService.getTopBookScores();

		assertNotNull(result);
		assertEquals(2, result.size());

		assertEquals(1L, result.getFirst().bookId());
		assertEquals("thumbnail1.jpg", result.get(0).thumbnailUrl());
		assertEquals("Book 1", result.get(0).title());

		assertEquals(2L, result.get(1).bookId());
		assertEquals("thumbnail2.jpg", result.get(1).thumbnailUrl());
		assertEquals("Book 2", result.get(1).title());

		verify(bookRepository, times(1)).findTopBooksByScore();
	}

	@Test
	@DisplayName("getTopBookScores - 리뷰 평균 점수에 대한 빈 리스트 반환")
	void getTopBookScoresNull() {
		when(bookRepository.findTopBooksByScore()).thenReturn(null);

		List<TopBookResponse> responses = bookService.getTopBookScores();

		assertNotNull(responses);
		assertTrue(responses.isEmpty());

		verify(bookRepository, times(1)).findTopBooksByScore();
	}

	@Test
	@DisplayName("getAllBooks - 모든 도서 반환")
	void getAllBooks() {
		Book book1 = Book.builder()
			.title("Book 1")
			.publisherId(publisher)
			.thumbnailImageUrl("thumbnail1.jpg")
			.publicationDate(LocalDate.of(2022, 1, 1))
			.price(BigDecimal.valueOf(20000))
			.discountRate(10)
			.description("Description 1")
			.contents("Contents 1")
			.isbn("1234567890123")
			.isPackageable(true)
			.likeCount(0)
			.stock(100)
			.viewCount(0L)
			.build();

		ReflectionTestUtils.setField(book, "id", 1L);

		Book book2 = Book.builder()
			.title("Book 2")
			.publisherId(publisher)
			.thumbnailImageUrl("thumbnail2.jpg")
			.publicationDate(LocalDate.of(2023, 1, 1))
			.price(BigDecimal.valueOf(25000))
			.discountRate(15)
			.description("Description 2")
			.contents("Contents 2")
			.isbn("1234567890124")
			.isPackageable(true)
			.likeCount(0)
			.stock(50)
			.viewCount(0L)
			.build();

		ReflectionTestUtils.setField(book, "id", 2L);

		List<Book> books = List.of(book1, book2);
		when(bookRepository.findAllAndDeletedAtIsNull()).thenReturn(books);

		BookResponse response1 = new BookResponse(
			1L,
			"누리북스",
			"정상",
			"Book 1",
			"thumbnail1.jpg",
			LocalDate.of(2022, 1, 1),
			BigDecimal.valueOf(20000),
			10,
			BigDecimal.valueOf(18000),
			"Description 1",
			"Contents 1",
			"1234567890123",
			true,
			0,
			100,
			0L,
			List.of("tag1", "tag2"),
			Map.of("지은이", List.of("Author1")),
			List.of(List.of(new SimpleCategoryResponse(1L, "소설"), new SimpleCategoryResponse(2L, "한국소설")))
		);

		BookResponse response2 = new BookResponse(
			2L,
			"누리북스",
			"정상",
			"Book 2",
			"thumbnail2.jpg",
			LocalDate.of(2023, 1, 1),
			BigDecimal.valueOf(25000),
			15,
			BigDecimal.valueOf(21250),
			"Description 2",
			"Contents 2",
			"1234567890124",
			true,
			0,
			50,
			0L,
			List.of("tag3"),
			Map.of("지은이", List.of("Author2")),
			List.of(List.of(new SimpleCategoryResponse(1L, "비소설"), new SimpleCategoryResponse(2L, "기타")))
		);

		when(bookMapper.toBookResponse(book1)).thenReturn(response1);
		when(bookMapper.toBookResponse(book2)).thenReturn(response2);

		List<BookResponse> result = bookService.getAllBooks();

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(response1, result.get(0));
		assertEquals(response2, result.get(1));

		verify(bookRepository, times(1)).findAllAndDeletedAtIsNull();
		verify(bookMapper, times(1)).toBookResponse(book1);
		verify(bookMapper, times(1)).toBookResponse(book2);
	}
}