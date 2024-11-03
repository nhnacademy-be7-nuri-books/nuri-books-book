package shop.nuribooks.books.book.booktag.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.book.entitiy.BookStateEnum;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.booktag.dto.BookTagGetResponse;
import shop.nuribooks.books.book.booktag.dto.BookTagRequest;
import shop.nuribooks.books.book.booktag.dto.BookTagRegisterResponse;
import shop.nuribooks.books.book.booktag.entity.BookTag;
import shop.nuribooks.books.book.booktag.repository.BookTagRepository;
import shop.nuribooks.books.book.publisher.entitiy.Publisher;
import shop.nuribooks.books.book.tag.entity.Tag;
import shop.nuribooks.books.book.tag.repository.TagRepository;
import shop.nuribooks.books.exception.book.BookNotFoundException;
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

	private Book book;
	private Book book1;

	private Tag tag1;
	private Tag tag2;

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

		book1 = Book.builder()
				.publisherId(new Publisher(1L, "Sample Publisher"))
				.state(BookStateEnum.NEW) // state 값 설정
				.title("Sample Book1")
				.thumbnailImageUrl("https://example.com/thumbnail.jpg")
				.detailImageUrl("https://example.com/detail.jpg")
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

		tag1 = Tag.builder().id(2L).name("study").build();
		tag2 = Tag.builder().id(3L).name("math").build();
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

		verify(bookRepository, times(1)).findById(bookId);
		verify(bookTagRepository, times(1)).findTagNamesByBookId(bookId);
		verify(bookTagRepository, times(1)).findByBookId(bookId); // Verify findByBookId was called
	}

	@DisplayName("도서 태그 등록 실패 - 도서 없음")
	@Test
	public void failed_getBookTag() {
		// Given
		Long bookId = 1L;

		// Mocking the behavior of repositories
		when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

		// When / Then
		assertThrows(BookNotFoundException.class, () -> {
			bookTagService.getBookTag(bookId);
		});

		// Verify interactions
		verify(bookRepository, times(1)).findById(bookId);
		verify(bookTagRepository, never()).findTagNamesByBookId(anyLong());
	}

	@DisplayName("도서에 등록된 태그 조회 실패 - bookTagId는 null")
	@Test
	public void failed1_getBookTag() {
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

		verify(bookRepository, times(1)).findById(bookId);
		verify(bookTagRepository, times(1)).findTagNamesByBookId(bookId);
		verify(bookTagRepository, times(1)).findByBookId(bookId);
	}

}
