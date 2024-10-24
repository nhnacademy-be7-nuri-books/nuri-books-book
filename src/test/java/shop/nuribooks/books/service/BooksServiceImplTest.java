package shop.nuribooks.books.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.nuribooks.books.dto.book.BookRegisterReq;
import shop.nuribooks.books.dto.book.BookRegisterRes;
import shop.nuribooks.books.entity.book.Book;
import shop.nuribooks.books.entity.book.BookState;
import shop.nuribooks.books.entity.book.Publisher;
import shop.nuribooks.books.exception.BadRequestException;
import shop.nuribooks.books.exception.ResourceNotFoundException;
import shop.nuribooks.books.exception.book.ResourceAlreadyExistIsbnException;
import shop.nuribooks.books.repository.book.BookRepository;
import shop.nuribooks.books.repository.book.BookStateRepository;
import shop.nuribooks.books.repository.publisher.PublisherRepository;
import shop.nuribooks.books.service.book.impl.BookServiceImpl;

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

	private BookRegisterReq reqDto;

	private BookState bookStates;

	// @BeforeEach
	// public void setUp() {
	// 	bookStates = new BookState(1L, BookStateEnum.InStock);
	//
	// 	reqDto = new BookRegisterReq(
	// 		1L,
	// 		1L,
	// 		"Book Title",
	// 		"thumbnail.jpg",
	// 		"detail.jpg",
	// 		LocalDate.now(),
	// 		BigDecimal.valueOf(20000),
	// 		10,
	// 		"Book Description",
	// 		"Book Contents",
	// 		"1234567890123",
	// 		true,
	// 		100
	// 	);
	// }

	@Test
	public void registerBook_ShouldReturnResponse_WhenValidRequest() {
		Publisher mockPublisher = new Publisher(1L, "Publisher Name");

		when(bookStatesRepository.findById(1L)).thenReturn(Optional.of(bookStates));
		when(publishersRepository.findById(1L)).thenReturn(Optional.of(mockPublisher));
		when(booksRepository.existsByIsbn(reqDto.getIsbn())).thenReturn(false);
		when(booksRepository.save(any(Book.class))).thenAnswer(invocation -> {
			Book book = invocation.getArgument(0);
			book.setId(1L);
			return book;
		});

		BookRegisterRes result = booksService.registerBook(reqDto);

		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("Book Title", result.getTitle());
		verify(booksRepository, times(1)).save(any(Book.class));
	}

	@Test
	public void registerBook_ShouldThrowBadRequestException_WhenRequestIsNull() {
		assertThrows(BadRequestException.class, () -> booksService.registerBook(null));
	}

	@Test
	public void registerBook_ShouldThrowResourceNotFoundException_WhenBookStateNotFound() {
		when(bookStatesRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> booksService.registerBook(reqDto));
		verify(bookStatesRepository, times(1)).findById(1L);
	}

	@Test
	public void registerBook_ShouldThrowResourceNotFoundException_WhenPublisherNotFound() {
		when(bookStatesRepository.findById(1L)).thenReturn(Optional.of(bookStates));
		when(publishersRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> booksService.registerBook(reqDto));
		verify(bookStatesRepository, times(1)).findById(1L);
		verify(publishersRepository, times(1)).findById(1L);
	}

	@Test
	public void registerBook_ShouldThrowDuplicateIsbnException_WhenIsbnAlreadyExists() {
		when(booksRepository.existsByIsbn(reqDto.getIsbn())).thenReturn(true);

		assertThrows(ResourceAlreadyExistIsbnException.class, () -> booksService.registerBook(reqDto));

		verify(booksRepository, times(1)).existsByIsbn(reqDto.getIsbn());
	}

	@Test
	public void registerBook_ShouldSaveBook_WhenBookStateAndPublisherFound() {
		Publisher mockPublisher = new Publisher(1L, "Publisher Name");

		when(bookStatesRepository.findById(1L)).thenReturn(Optional.of(bookStates));
		when(publishersRepository.findById(1L)).thenReturn(Optional.of(mockPublisher));
		when(booksRepository.existsByIsbn(reqDto.getIsbn())).thenReturn(false);

		Book mockBook = Book.builder()
			.id(null)
			.stateId(bookStates)
			.publisherId(mockPublisher)
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

		BookRegisterRes result = booksService.registerBook(reqDto);

		assertNotNull(result);
		assertEquals("Book Title", result.getTitle());
		verify(booksRepository, times(1)).save(any(Book.class));
	}
}
