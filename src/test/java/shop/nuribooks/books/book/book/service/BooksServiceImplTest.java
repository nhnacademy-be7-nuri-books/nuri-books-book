package shop.nuribooks.books.book.book.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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

import shop.nuribooks.books.book.book.dto.BookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BookRegisterResponse;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.dto.BookUpdateRequest;
import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.book.entitiy.BookStateEnum;
import shop.nuribooks.books.book.publisher.entitiy.Publisher;
import shop.nuribooks.books.exception.book.BookIdNotFoundException;
import shop.nuribooks.books.exception.book.PublisherIdNotFoundException;
import shop.nuribooks.books.exception.book.ResourceAlreadyExistIsbnException;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.publisher.repository.PublisherRepository;
import shop.nuribooks.books.book.book.service.impl.BookServiceImpl;

@ExtendWith(MockitoExtension.class)
public class BooksServiceImplTest {

	@InjectMocks
	private BookServiceImpl bookService;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private PublisherRepository publisherRepository;

	private BookRegisterRequest reqDto;
	private BookUpdateRequest updateRequest;
	private Publisher publisher;
	private Book book;

	@BeforeEach
	public void setUp() {
		publisher = new Publisher(1L, "Publisher Name");

		reqDto = new BookRegisterRequest(
			1L,
			BookStateEnum.NORMAL,
			"Book Title",
			"thumbnail.jpg",
			"detail.jpg",
			LocalDate.now(),
			BigDecimal.valueOf(20000),
			10,
			"Book Description",
			"Book Contents",
			"1234567890123",
			true,
			100
		);

		updateRequest = new BookUpdateRequest(
			1L,
			BookStateEnum.NEW,
			"Updated Book Title",
			"updated_thumbnail.jpg",
			"updated_detail.jpg",
			LocalDate.now(),
			BigDecimal.valueOf(25000),
			15,
			"Updated Description",
			"Updated Contents",
			"0987654321098",
			true,
			50
		);

		book = Book.builder()
			.publisherId(publisher)
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

		ReflectionTestUtils.setField(book, "id", 1L);
	}

	@Test
	public void registerBook_ShouldReturnResponse_WhenValidRequest() {
		when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));
		when(bookRepository.existsByIsbn(reqDto.isbn())).thenReturn(false);
		when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

		BookRegisterResponse result = bookService.registerBook(reqDto);

		assertNotNull(result);
		assertEquals("Book Title", result.title());
		verify(bookRepository, times(1)).save(any(Book.class));
	}

	@Test
	public void registerBook_ShouldThrowPublisherIdNotFoundException_WhenPublisherNotFound() {
		when(publisherRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(PublisherIdNotFoundException.class, () -> bookService.registerBook(reqDto));
	}

	@Test
	public void registerBook_ShouldThrowResourceAlreadyExistIsbnException_WhenIsbnAlreadyExists() {
		when(bookRepository.existsByIsbn(reqDto.isbn())).thenReturn(true);

		assertThrows(ResourceAlreadyExistIsbnException.class, () -> bookService.registerBook(reqDto));
	}

	@Test
	public void getBooks_ShouldReturnEmptyPage_WhenRequestingFirstPageButNoBooks() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<Book> emptyPage = new PageImpl<>(List.of(), pageable, 0);

		when(bookRepository.findAll(pageable)).thenReturn(emptyPage);

		Page<BookResponse> result = bookService.getBooks(pageable);
		assertTrue(result.isEmpty());
		verify(bookRepository, times(1)).findAll(pageable);
	}

	@Test
	public void getBookById_ShouldReturnBookResponseAndIncrementViewCount_WhenBookExists() {
		Long bookId = 1L;
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

		BookResponse result = bookService.getBookById(bookId);

		assertNotNull(result);
		assertEquals(bookId, result.id());
		assertEquals("Original Book Title", result.title());
		assertEquals(1L, book.getViewCount());

		verify(bookRepository, times(1)).save(book);
	}

	@Test
	public void getBookById_ShouldThrowBookIdNotFoundException_WhenBookDoesNotExist() {
		Long bookId = 9999L;
		when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

		assertThrows(BookIdNotFoundException.class, () -> bookService.getBookById(bookId));
		verify(bookRepository, never()).save(any());
	}

	@Test
	public void updateBook_ShouldUpdateBook_WhenValidRequest() {
		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));

		bookService.updateBook(1L, updateRequest);

		assertEquals("Updated Book Title", book.getTitle());
		assertEquals("updated_thumbnail.jpg", book.getThumbnailImageUrl());
		assertEquals(BigDecimal.valueOf(25000), book.getPrice());
		verify(bookRepository, times(1)).save(book);
	}

	@Test
	public void updateBook_ShouldThrowBookIdNotFoundException_WhenBookNotFound() {
		when(bookRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(BookIdNotFoundException.class, () -> bookService.updateBook(1L, updateRequest));
	}

	@Test
	public void updateBook_ShouldThrowPublisherIdNotFoundException_WhenPublisherNotFound() {
		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(publisherRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(PublisherIdNotFoundException.class, () -> bookService.updateBook(1L, updateRequest));
	}

	@Test
	public void deleteBook_ShouldDeleteBook_WhenBookExists() {
		Long bookId = 1L;
		when(bookRepository.existsById(bookId)).thenReturn(true);

		assertDoesNotThrow(() -> bookService.deleteBook(bookId));
		verify(bookRepository, times(1)).deleteById(bookId);
	}

	@Test
	public void deleteBook_ShouldThrowBookIdNotFoundException_WhenBookDoesNotExist() {
		Long bookId = 9999L;
		when(bookRepository.existsById(bookId)).thenReturn(false);

		assertThrows(BookIdNotFoundException.class, () -> bookService.deleteBook(bookId));
		verify(bookRepository, never()).deleteById(bookId);
	}
}
