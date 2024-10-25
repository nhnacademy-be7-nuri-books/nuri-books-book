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
import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.bookstate.entitiy.BookState;
import shop.nuribooks.books.book.publisher.entitiy.Publisher;
import shop.nuribooks.books.exception.BadRequestException;
import shop.nuribooks.books.exception.book.BookStatesIdNotFoundException;
import shop.nuribooks.books.exception.book.PublisherIdNotFoundException;
import shop.nuribooks.books.exception.book.ResourceAlreadyExistIsbnException;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.bookstate.repository.BookStateRepository;
import shop.nuribooks.books.book.publisher.repository.PublisherRepository;
import shop.nuribooks.books.book.book.service.impl.BookServiceImpl;

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

	private BookState bookState;
	private Publisher publisher;

	@BeforeEach
	public void setUp() {
		bookState = BookState.builder()
			.detail("InStock")
			.build();
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
	}

	@Test
	public void registerBook_ShouldReturnResponse_WhenValidRequest() {
		when(bookStatesRepository.findById(1)).thenReturn(Optional.of(bookState));
		when(publishersRepository.findById(1L)).thenReturn(Optional.of(publisher));
		when(booksRepository.existsByIsbn(reqDto.isbn())).thenReturn(false);
		when(booksRepository.save(any(Book.class))).thenAnswer(invocation -> {
			Book book = invocation.getArgument(0);
			return book;
		});

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
		verify(bookStatesRepository, times(1)).findById(1);
	}

	@Test
	public void registerBook_ShouldThrowPublisherIdNotFoundException_WhenPublisherNotFound() {
		when(bookStatesRepository.findById(1)).thenReturn(Optional.of(bookState));
		when(publishersRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(PublisherIdNotFoundException.class, () -> booksService.registerBook(reqDto));
		verify(publishersRepository, times(1)).findById(1L);
	}

	@Test
	public void registerBook_ShouldThrowResourceAlreadyExistIsbnException_WhenIsbnAlreadyExists() {
		when(booksRepository.existsByIsbn(reqDto.isbn())).thenReturn(true);

		assertThrows(ResourceAlreadyExistIsbnException.class, () -> booksService.registerBook(reqDto));
		verify(booksRepository, times(1)).existsByIsbn(reqDto.isbn());
	}

	@Test
	public void registerBook_ShouldSaveBook_WhenBookStateAndPublisherFound() {
		when(bookStatesRepository.findById(1)).thenReturn(Optional.of(bookState));
		when(publishersRepository.findById(1L)).thenReturn(Optional.of(publisher));
		when(booksRepository.existsByIsbn(reqDto.isbn())).thenReturn(false);

		Book mockBook = Book.builder()
			.stateId(bookState)
			.publisherId(publisher)
			.title("Book Title")
			.thumbnailImageUrl("thumbnail.jpg")
			.detailImageUrl("detail.jpg")
			.publicationDate(LocalDate.now())
			.price(BigDecimal.valueOf(20000))
			.discountRate(10)
			.description("Book Description")
			.contents("Book Contents")
			.isbn("1234567890123")
			.isPackageable(true)
			.likeCount(0)
			.stock(100)
			.viewCount(0L)
			.build();

		when(booksRepository.save(any(Book.class))).thenReturn(mockBook);

		BookRegisterResponse result = booksService.registerBook(reqDto);

		assertNotNull(result);
		assertEquals("Book Title", result.title());
		verify(booksRepository, times(1)).save(any(Book.class));
	}
}
