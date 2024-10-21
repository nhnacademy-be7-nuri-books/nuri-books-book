package shop.nuribooks.books.service;

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

import shop.nuribooks.books.dto.books.BooksRegisterReqDto;
import shop.nuribooks.books.dto.books.BooksRegisterResDto;
import shop.nuribooks.books.entity.BookStates;
import shop.nuribooks.books.entity.BookStatesEnum;
import shop.nuribooks.books.entity.Books;
import shop.nuribooks.books.entity.Publishers;
import shop.nuribooks.books.exception.BadRequestException;
import shop.nuribooks.books.exception.ResourceNotFoundException;
import shop.nuribooks.books.repository.books.BookStatesRepository;
import shop.nuribooks.books.repository.books.BooksRepository;
import shop.nuribooks.books.repository.books.PublishersRepository;
import shop.nuribooks.books.service.books.impl.BooksServiceImpl;

@ExtendWith(MockitoExtension.class)
public class BooksServiceImplTest {

	@InjectMocks
	private BooksServiceImpl booksService;

	@Mock
	private BooksRepository booksRepository;

	@Mock
	private BookStatesRepository bookStatesRepository;

	@Mock
	private PublishersRepository publishersRepository;

	private BooksRegisterReqDto reqDto;

	private BookStates bookStates;

	@BeforeEach
	public void setUp() {
		bookStates = new BookStates(1L, BookStatesEnum.InStock);

		reqDto = new BooksRegisterReqDto(
			1L,
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
		Publishers mockPublisher = new Publishers(1L, "Publisher Name");

		when(bookStatesRepository.findById(1L)).thenReturn(Optional.of(bookStates));
		when(publishersRepository.findById(1L)).thenReturn(Optional.of(mockPublisher));
		when(booksRepository.save(any(Books.class))).thenAnswer(invocation -> {
			Books book = invocation.getArgument(0);
			book.setId(1L);
			return book;
		});

		BooksRegisterResDto result = booksService.registerBook(reqDto);

		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("Book Title", result.getTitle());
		verify(booksRepository, times(1)).save(any(Books.class));
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
	public void registerBook_ShouldSaveBook_WhenBookStateAndPublisherFound() {
		Publishers mockPublisher = new Publishers(1L, "Publisher Name");

		when(bookStatesRepository.findById(1L)).thenReturn(Optional.of(bookStates));
		when(publishersRepository.findById(1L)).thenReturn(Optional.of(mockPublisher));

		Books mockBook = new Books(null, bookStates, mockPublisher, "Book Title",
			"thumbnail.jpg", "detail.jpg", LocalDate.now(), BigDecimal.valueOf(20000),
			10, "Book Description", "Book Contents", "1234567890123",
			true, 0, 100, 0L);

		when(booksRepository.save(any(Books.class))).thenReturn(mockBook);

		BooksRegisterResDto result = booksService.registerBook(reqDto);

		assertNotNull(result);
		assertEquals("Book Title", result.getTitle());
		verify(booksRepository, times(1)).save(any(Books.class));
	}
}
