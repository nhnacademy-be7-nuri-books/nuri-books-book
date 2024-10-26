package shop.nuribooks.books.book.book.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.nuribooks.books.book.book.dto.BookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BookRegisterResponse;
import shop.nuribooks.books.book.book.dto.BookUpdateRequest;
import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.bookstate.entitiy.BookState;
import shop.nuribooks.books.book.publisher.entitiy.Publisher;
import shop.nuribooks.books.exception.BadRequestException;
import shop.nuribooks.books.exception.book.BookIdNotFoundException;
import shop.nuribooks.books.exception.book.BookStatesIdNotFoundException;
import shop.nuribooks.books.exception.book.PublisherIdNotFoundException;
import shop.nuribooks.books.exception.book.ResourceAlreadyExistIsbnException;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.bookstate.repository.BookStateRepository;
import shop.nuribooks.books.book.publisher.repository.PublisherRepository;
import shop.nuribooks.books.book.book.service.impl.BookServiceImpl;
import shop.nuribooks.books.exception.bookstate.BookStateIdNotFoundException;

@ExtendWith(MockitoExtension.class)
public class BooksServiceImplTest {

	@InjectMocks
	private BookServiceImpl booksService;

	@Mock
	private BookRepository booksRepository;

	@Mock
	private BookStateRepository bookStatesRepository;

	@Mock
	private PublisherRepository publishersRepository;

	private BookRegisterRequest reqDto;
	private BookUpdateRequest updateRequest;
	private BookState bookState;
	private Publisher publisher;
	private Book book;

	@BeforeEach
	public void setUp() {
		bookState = BookState.builder().detail("InStock").build();
		publisher = new Publisher(1L, "Publisher Name");

		reqDto = new BookRegisterRequest(
			1,
			1L,
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
			1,
			1L,
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
			.stateId(bookState)
			.publisherId(publisher)
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

	@Test
	public void registerBook_ShouldReturnResponse_WhenValidRequest() {
		when(bookStatesRepository.findById(1)).thenReturn(Optional.of(bookState));
		when(publishersRepository.findById(1L)).thenReturn(Optional.of(publisher));
		when(booksRepository.existsByIsbn(reqDto.isbn())).thenReturn(false);
		when(booksRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

		BookRegisterResponse result = booksService.registerBook(reqDto);

		assertNotNull(result);
		assertEquals("Book Title", result.title());
		verify(booksRepository, times(1)).save(any(Book.class));
	}

	@Test
	public void registerBook_ShouldThrowBadRequestException_WhenRequestIsNull() {
		assertThrows(BadRequestException.class, () -> booksService.registerBook(null));
	}

	@Test
	public void registerBook_ShouldThrowBookStateIdNotFoundException_WhenBookStateNotFound() {
		when(bookStatesRepository.findById(1)).thenReturn(Optional.empty());
		assertThrows(BookStatesIdNotFoundException.class, () -> booksService.registerBook(reqDto));
	}

	@Test
	public void registerBook_ShouldThrowPublisherIdNotFoundException_WhenPublisherNotFound() {
		when(bookStatesRepository.findById(1)).thenReturn(Optional.of(bookState));
		when(publishersRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(PublisherIdNotFoundException.class, () -> booksService.registerBook(reqDto));
	}

	@Test
	public void registerBook_ShouldThrowResourceAlreadyExistIsbnException_WhenIsbnAlreadyExists() {
		when(booksRepository.existsByIsbn(reqDto.isbn())).thenReturn(true);
		assertThrows(ResourceAlreadyExistIsbnException.class, () -> booksService.registerBook(reqDto));
	}

	@Test
	public void updateBook_ShouldUpdateBook_WhenValidRequest() {
		when(booksRepository.findById(1L)).thenReturn(Optional.of(book));
		when(bookStatesRepository.findById(1)).thenReturn(Optional.of(bookState));
		when(publishersRepository.findById(1L)).thenReturn(Optional.of(publisher));

		booksService.updateBook(1L, updateRequest);

		assertEquals("Updated Book Title", book.getTitle());
		assertEquals("updated_thumbnail.jpg", book.getThumbnailImageUrl());
		assertEquals(BigDecimal.valueOf(25000), book.getPrice());
		verify(booksRepository, times(1)).save(book);
	}

	@Test
	public void updateBook_ShouldThrowBookIdNotFoundException_WhenBookNotFound() {
		when(booksRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(BookIdNotFoundException.class, () -> booksService.updateBook(1L, updateRequest));
	}

	@Test
	public void updateBook_ShouldThrowBookStateIdNotFoundException_WhenBookStateNotFound() {
		when(booksRepository.findById(1L)).thenReturn(Optional.of(book));
		when(bookStatesRepository.findById(1)).thenReturn(Optional.empty());

		assertThrows(BookStateIdNotFoundException.class, () -> booksService.updateBook(1L, updateRequest));
	}

	@Test
	public void updateBook_ShouldThrowPublisherIdNotFoundException_WhenPublisherNotFound() {
		when(booksRepository.findById(1L)).thenReturn(Optional.of(book));
		when(bookStatesRepository.findById(1)).thenReturn(Optional.of(bookState));
		when(publishersRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(PublisherIdNotFoundException.class, () -> booksService.updateBook(1L, updateRequest));
	}
}
