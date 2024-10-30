package shop.nuribooks.books.book.booktag.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.booktag.dto.BookTagRequest;
import shop.nuribooks.books.book.booktag.dto.BookTagResponse;
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
	private Tag tag1;
	private Tag tag2;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		book = Book.builder()
			.publisherId(new Publisher(1L, "Sample Publisher"))
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
		tag1 = Tag.builder().id(2L).name("study").build();
		tag2 = Tag.builder().id(3L).name("math").build();
	}

	@Test
	void registerTagToBook() {
		// Given
		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(tagRepository.findById(2L)).thenReturn(Optional.of(tag1));
		when(tagRepository.findById(3L)).thenReturn(Optional.of(tag2));

		BookTagRequest request = new BookTagRequest(1L, List.of(2L, 3L));

		// When
		BookTagResponse response = bookTagService.registerTagToBook(request);

		// Then
		assertNotNull(response);
		assertEquals(1L, response.bookId());
		assertEquals(List.of(2L, 3L), response.tagId());
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
}
