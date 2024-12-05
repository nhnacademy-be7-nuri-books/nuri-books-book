package shop.nuribooks.books.book.booktag.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.entity.BookStateEnum;
import shop.nuribooks.books.book.book.mapper.BookMapper;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.booktag.dto.BookTagGetResponse;
import shop.nuribooks.books.book.booktag.dto.BookTagRegisterResponse;
import shop.nuribooks.books.book.booktag.dto.BookTagRequest;
import shop.nuribooks.books.book.booktag.entity.BookTag;
import shop.nuribooks.books.book.booktag.repository.BookTagRepository;
import shop.nuribooks.books.book.publisher.entity.Publisher;
import shop.nuribooks.books.book.tag.entity.Tag;
import shop.nuribooks.books.book.tag.repository.TagRepository;
import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.tag.BookTagAlreadyExistsException;
import shop.nuribooks.books.exception.tag.BookTagNotFountException;
import shop.nuribooks.books.exception.tag.TagNotFoundException;

class BookTagServiceImplTest {

	@InjectMocks
	private BookTagServiceImpl bookTagService;

	@Mock
	private BookTagRepository bookTagRepository;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private TagRepository tagRepository;

	@Mock
	private BookMapper bookMapper;

	private Book book;
	private Book book1;

	private Tag tag1;
	private Tag tag2;

	private BookTag bookTag;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		book = Book.builder()
			.publisherId(new Publisher(1L, "Sample Publisher"))
			.state(BookStateEnum.NEW)
			.title("Sample Book")
			.thumbnailImageUrl("https://example.com/thumbnail.jpg")
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

		book1 = Book.builder()
			.publisherId(new Publisher(1L, "Sample Publisher"))
			.state(BookStateEnum.NEW) // state 값 설정
			.title("Sample Book1")
			.thumbnailImageUrl("https://example.com/thumbnail.jpg")
			.publicationDate(LocalDate.now())
			.price(BigDecimal.valueOf(29.99))
			.discountRate(10)
			.description("Sample description.")
			.contents("Sample contents.")
			.isbn("978-3-16-148410-1")
			.isPackageable(true)
			.stock(100)
			.likeCount(0)
			.viewCount(0L)
			.build();

		TestUtils.setIdForEntity(book, 1L);
		TestUtils.setIdForEntity(book1, 2L);

		tag1 = Tag.builder().id(2L).name("study").build();
		tag2 = Tag.builder().id(3L).name("math").build();

		bookTag = BookTag.builder()
			.book(book)
			.tag(tag1)
			.build();
	}

	@DisplayName("도서 태그 등록 성공")
	@Test
	void registerTagToBook() {
		// Given
		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(tagRepository.findById(2L)).thenReturn(Optional.of(tag1));
		when(tagRepository.findById(3L)).thenReturn(Optional.of(tag2));

		BookTagRequest request = new BookTagRequest(1L, List.of(2L, 3L));

		// When
		BookTagRegisterResponse response = bookTagService.registerTagToBook(request);

		// Then
		assertNotNull(response);
		assertEquals(1L, response.bookId());
		assertEquals(List.of(2L, 3L), response.tagIds());
		verify(bookTagRepository, times(2)).save(any(BookTag.class));
	}

	@DisplayName("도서 태그 등록 실패 - 책 없음")
	@Test
	void failed_registerTagToBook() {
		// Given
		when(bookRepository.findById(1L)).thenReturn(Optional.empty());
		BookTagRequest request = new BookTagRequest(1L, List.of(2L, 3L));

		// When & Then
		assertThrows(BookNotFoundException.class, () -> bookTagService.registerTagToBook(request));
	}

	@DisplayName("도서 태그 등록 실패 - 태그 없음")
	@Test
	void failed_registerTagToBook_TagNotFound() {
		// Given
		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(tagRepository.findById(2L)).thenReturn(Optional.empty());
		BookTagRequest request = new BookTagRequest(1L, List.of(2L, 3L));

		// When & Then
		assertThrows(TagNotFoundException.class, () -> bookTagService.registerTagToBook(request));
	}

	@DisplayName("도서 태그 등록 실패 - 중복")
	@Test
	void failed_registerTagToBook_BookTagAlreadyExists() {
		// Given
		Long bookId = 1L;
		List<Long> tagIds = List.of(tag1.getId(), tag2.getId());
		BookTagRequest request = new BookTagRequest(bookId, tagIds);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(tagRepository.findById(tag1.getId())).thenReturn(Optional.of(tag1));
		when(tagRepository.findById(tag2.getId())).thenReturn(Optional.of(tag2));

		when(bookTagRepository.existsByBookIdAndTagId(bookId, tag1.getId())).thenReturn(true);

		// When & Then
		BookTagAlreadyExistsException exception = assertThrows(BookTagAlreadyExistsException.class, () -> {
			bookTagService.registerTagToBook(request);
		});

		assertEquals("해당 도서에 이미 등록된 태그입니다.", exception.getMessage());

		verify(bookTagRepository, times(1)).existsByBookIdAndTagId(bookId, tag1.getId());
		verify(bookTagRepository, never()).existsByBookIdAndTagId(bookId, tag2.getId());
		verify(bookTagRepository, never()).save(any(BookTag.class));
	}

	@DisplayName("도서 태그 조회 성공")
	@Test
	public void getBookTag() {
		// Given
		Long bookId = 1L;

		// Mocking the behavior of repositories
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(bookTagRepository.findTagNamesByBookId(bookId)).thenReturn(Arrays.asList("magazine", "study"));

		// Mocking the behavior for finding BookTags
		BookTag bookTag1 = BookTag.builder().book(book).tag(tag1).build();
		BookTag bookTag2 = BookTag.builder().book(book).tag(tag2).build();
		when(bookTagRepository.findByBookId(bookId)).thenReturn(Arrays.asList(bookTag1, bookTag2));

		// When
		BookTagGetResponse response = bookTagService.getBookTag(bookId);

		// Then
		assertNotNull(response);
		assertEquals(bookId, response.bookId());
		assertEquals(Arrays.asList("magazine", "study"), response.tagNames());

		verify(bookTagRepository, times(1)).findTagNamesByBookId(bookId);
		verify(bookTagRepository, times(1)).findByBookId(bookId); // Verify findByBookId was called
	}

	@DisplayName("도서 태그 등록 실패 - 도서 없음")
	@Test
	void failed_getBookTag() {
		// Given
		Long bookId = 1L;

		// Mocking the behavior of repositories
		when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

		// Verify interactions
		verify(bookTagRepository, never()).findTagNamesByBookId(anyLong());
	}

	@DisplayName("도서에 등록된 태그 조회 실패 - bookTagId는 null")
	@Test
	void failed1_getBookTag() {
		// Given
		Long bookId = 1L;

		// Mocking the behavior of repositories
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(bookTagRepository.findTagNamesByBookId(bookId)).thenReturn(Arrays.asList("magazine", "study"));
		when(bookTagRepository.findByBookId(bookId)).thenReturn(Collections.emptyList());

		// When
		BookTagGetResponse response = bookTagService.getBookTag(bookId);

		// Then
		assertNotNull(response);
		assertEquals(bookId, response.bookId());
		assertNull(response.bookTagId()); // bookTagId가 null인지 확인
		assertEquals(Arrays.asList("magazine", "study"), response.tagNames());

		verify(bookTagRepository, times(1)).findTagNamesByBookId(bookId);
		verify(bookTagRepository, times(1)).findByBookId(bookId);
	}

	@DisplayName("태그로 도서 조회 성공")
	@Test
	void getBooksByTagId_Success() {
		// Given
		Long tagId = tag1.getId();
		when(bookTagRepository.findBookIdsByTagId(tagId)).thenReturn(List.of(book.getId(), book1.getId()));

		when(bookRepository.findAllById(List.of(book.getId(), book1.getId()))).thenReturn(Arrays.asList(book, book1));
		when(bookMapper.toBookResponse(any())).thenReturn(null);

		// When
		List<BookResponse> bookResponses = bookTagService.getBooksByTagId(tagId);

		// Then
		assertEquals(2, bookResponses.size());
	}

	@DisplayName("태그로 도서 조회 실패 - 태그가 존재하지 않음")
	@Test
	void getBooksByTagId_TagNotFound() {
		// Given
		Long tagId = 999L;
		when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

		verify(bookTagRepository, never()).findBookIdsByTagId(anyLong());
		verify(bookRepository, never()).findAllById(anyList());
	}

	@DisplayName("도서 태그 삭제 성공")
	@Test
	void deleteBookTag_Success() {
		// Given
		Long bookTagId = 1L;
		when(bookTagRepository.findById(bookTagId)).thenReturn(Optional.of(bookTag));

		// When
		bookTagService.deleteBookTag(bookTagId);

		// Then
		verify(bookTagRepository, times(1)).delete(bookTag);
	}

	@DisplayName("도서 태그 삭제 실패 - 태그가 존재하지 않음")
	@Test
	void deleteBookTag_NotFound() {
		// Given
		Long bookTagId = 999L; // 존재하지 않는 ID
		when(bookTagRepository.findById(bookTagId)).thenReturn(Optional.empty());

		// When & Then
		assertThrows(BookTagNotFountException.class, () -> bookTagService.deleteBookTag(bookTagId));
		verify(bookTagRepository, never()).delete(any(BookTag.class));
	}

	@DisplayName("도서 태그 삭제 성공")
	@Test
	void deleteBookTagIds_Success() {
		// Given
		Long bookId = 1L;
		when(bookTagRepository.findByBookId(bookId)).thenReturn(List.of(bookTag));

		// When
		bookTagService.deleteBookTagIds(bookId);

		// Then
		verify(bookTagRepository, times(1)).deleteAll(any());
	}

	@DisplayName("도서 태그 등록 성공")
	@Test
	void registerTagToBook_success() {
		// Given
		Long bookId = 1L;
		List<Long> tagIds = List.of(tag1.getId(), tag2.getId());
		// When
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(tagRepository.findById(anyLong())).thenReturn(Optional.of(tag1)).thenReturn(Optional.of(tag2));
		when(bookTagRepository.existsByBookIdAndTagId(anyLong(), anyLong())).thenReturn(false).thenReturn(false);
		when(bookTagRepository.save(any())).thenReturn(bookTag).thenReturn(bookTag);

		// Then
		bookTagService.registerTagToBook(bookId, tagIds);
		verify(bookTagRepository, times(2)).existsByBookIdAndTagId(anyLong(), anyLong());
	}

	@DisplayName("도서 태그 등록 실패")
	@Test
	void registerTagToBook_fail() {
		// Given
		Long bookId = 1L;
		List<Long> tagIds = List.of(tag1.getId(), tag2.getId());
		// When
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());

		// Then
		Assertions.assertThrows(TagNotFoundException.class,
			() -> bookTagService.registerTagToBook(bookId, tagIds));
	}

	@DisplayName("도서 태그 등록 실패")
	@Test
	void registerTagToBook_failAlreadyExist() {
		// Given
		Long bookId = 1L;
		List<Long> tagIds = List.of(tag1.getId(), tag2.getId());
		// When
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(tagRepository.findById(anyLong())).thenReturn(Optional.of(tag1));
		when(bookTagRepository.existsByBookIdAndTagId(anyLong(), anyLong())).thenReturn(true);

		// Then
		Assertions.assertThrows(BookTagAlreadyExistsException.class,
			() -> bookTagService.registerTagToBook(bookId, tagIds));
	}

	@DisplayName("도서 태그 등록 실패")
	@Test
	void registerTagToBook_failBookNotFound() {
		// Given
		Long bookId = 1L;
		List<Long> tagIds = List.of(tag1.getId(), tag2.getId());
		// When
		when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

		// Then
		Assertions.assertThrows(BookNotFoundException.class, () -> bookTagService.registerTagToBook(bookId, tagIds));
	}
}
