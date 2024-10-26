package shop.nuribooks.books.book.bookstate.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.bookstate.dto.BookStateRequest;
import shop.nuribooks.books.book.bookstate.dto.BookStateResponse;

import shop.nuribooks.books.book.bookstate.entitiy.BookState;
import shop.nuribooks.books.exception.bookstate.BookStateDetailAlreadyExistException;
import shop.nuribooks.books.exception.bookstate.BookStateIdNotFoundException;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.bookstate.repository.BookStateRepository;
import shop.nuribooks.books.book.bookstate.service.Impl.BookStateServiceImpl;

@ExtendWith(MockitoExtension.class)
public class BookStateServiceTest {

	@InjectMocks
	private BookStateServiceImpl bookStateService;

	@Mock
	private BookStateRepository bookStateRepository;

	@Mock
	private BookRepository bookRepository;

	private BookStateRequest bookStateRequest;
	private BookState bookState;
	private Book book;
	private static final Integer DEFAULT_STATE_ID = 1;

	@BeforeEach
	public void setUp() {
		bookStateRequest = new BookStateRequest("재고있음");
		bookState = BookState.builder()
			.detail("재고있음")
			.build();
		book = mock(Book.class);
	}

	@Test
	public void registerState_ShouldSaveBookState_WhenRequestIsValid() {
		String admin = "admin";
		when(bookStateRepository.existsBookStatesByDetail(bookStateRequest.detail())).thenReturn(false);

		bookStateService.registerState(admin, bookStateRequest);

		verify(bookStateRepository, times(1)).save(any(BookState.class));
		verify(bookStateRepository, times(1)).existsBookStatesByDetail(bookStateRequest.detail());
	}

	@Test
	public void registerState_ShouldThrowBookStateDetailAlreadyExistException_WhenStateAlreadyExists() {
		String admin = "admin";
		when(bookStateRepository.existsBookStatesByDetail(bookStateRequest.detail())).thenReturn(true);

		assertThrows(BookStateDetailAlreadyExistException.class, () -> bookStateService.registerState(admin, bookStateRequest));
	}

	@Test
	public void getAllBooks_ShouldReturnBookStateResponseList_WhenBookStatesExist() {
		when(bookStateRepository.findAll()).thenReturn(List.of(bookState));

		List<BookStateResponse> result = bookStateService.getAllBooks();

		assertEquals(1, result.size());
		assertEquals(bookState.getDetail(), result.get(0).detail());

		verify(bookStateRepository, times(1)).findAll();
	}

	@Test
	public void getAllBooks_ShouldReturnEmptyList_WhenNoBookStatesExist() {
		when(bookStateRepository.findAll()).thenReturn(List.of());

		List<BookStateResponse> result = bookStateService.getAllBooks();

		assertTrue(result.isEmpty());
		verify(bookStateRepository, times(1)).findAll();
	}

	@Test
	public void updateState_ShouldUpdateState_WhenValidIdAndRequest() {
		when(bookStateRepository.findById(1)).thenReturn(Optional.of(bookState));
		when(bookStateRepository.existsBookStatesByDetail(bookStateRequest.detail())).thenReturn(false);

		bookStateService.updateState(1, bookStateRequest);

		verify(bookStateRepository, times(1)).save(bookState);
		verify(bookStateRepository, times(1)).existsBookStatesByDetail(bookStateRequest.detail());
		verify(bookStateRepository, times(1)).findById(1);
	}

	@Test
	public void updateState_ShouldThrowBookStateDetailAlreadyExistException_WhenStateAlreadyExists() {
		when(bookStateRepository.findById(1)).thenReturn(Optional.of(bookState));
		when(bookStateRepository.existsBookStatesByDetail(bookStateRequest.detail())).thenReturn(true);

		assertThrows(BookStateDetailAlreadyExistException.class, () -> {
			bookStateService.updateState(1, bookStateRequest);
		});

		verify(bookStateRepository, times(1)).findById(1);
		verify(bookStateRepository, times(1)).existsBookStatesByDetail(bookStateRequest.detail());
		verify(bookStateRepository, never()).save(any(BookState.class));
	}

	@Test
	public void updateState_ShouldThrowBookStateIdNotFoundException_WhenStateNotFound() {
		when(bookStateRepository.findById(1)).thenReturn(Optional.empty());

		assertThrows(BookStateIdNotFoundException.class, () -> bookStateService.updateState(1, bookStateRequest));

		verify(bookStateRepository, times(1)).findById(1);
	}

	@Test
	public void deleteState_ShouldUpdateBooksToDefaultStateAndDeleteState_WhenStateExists() {
		BookState defaultState = BookState.builder()
			.detail("기본 상태")
			.build();
		when(bookStateRepository.findById(DEFAULT_STATE_ID)).thenReturn(Optional.of(defaultState));
		when(bookStateRepository.findById(1)).thenReturn(Optional.of(bookState));
		when(bookRepository.findByStateId(bookState)).thenReturn(List.of(book));

		bookStateService.deleteState(1);

		verify(bookRepository, times(1)).saveAll(anyList());
		verify(bookStateRepository, times(1)).deleteById(1);
	}

	@Test
	public void deleteState_ShouldThrowBookStateIdNotFoundException_WhenStateNotFound() {
		when(bookStateRepository.findById(1)).thenReturn(Optional.empty());

		assertThrows(BookStateIdNotFoundException.class, () -> bookStateService.deleteState(1));

		verify(bookStateRepository, times(1)).findById(1);
	}

	@Test
	public void deleteState_ShouldHandleEmptyBookList_WhenStateHasNoAssociatedBooks() {
		BookState defaultState = BookState.builder()
			.detail("기본 상태")
			.build();
		when(bookStateRepository.findById(DEFAULT_STATE_ID)).thenReturn(Optional.of(defaultState));
		when(bookStateRepository.findById(1)).thenReturn(Optional.of(bookState));
		when(bookRepository.findByStateId(bookState)).thenReturn(List.of());

		bookStateService.deleteState(1);

		verify(bookRepository, never()).saveAll(anyList());
		verify(bookStateRepository, times(1)).deleteById(1);
	}
}
