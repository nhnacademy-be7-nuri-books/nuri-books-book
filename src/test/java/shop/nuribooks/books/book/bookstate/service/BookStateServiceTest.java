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
import org.springframework.test.util.ReflectionTestUtils;

import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.bookstate.dto.BookStateRequest;
import shop.nuribooks.books.book.bookstate.dto.BookStateResponse;

import shop.nuribooks.books.book.bookstate.entitiy.BookState;
import shop.nuribooks.books.exception.DefaultStateDeletionException;
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
		when(bookStateRepository.existsBookStatesByDetail(bookStateRequest.detail())).thenReturn(false);

		bookStateService.registerState(bookStateRequest);

		verify(bookStateRepository, times(1)).save(any(BookState.class));
		verify(bookStateRepository, times(1)).existsBookStatesByDetail(bookStateRequest.detail());
	}

	@Test
	public void registerState_ShouldThrowBookStateDetailAlreadyExistException_WhenStateAlreadyExists() {
		when(bookStateRepository.existsBookStatesByDetail(bookStateRequest.detail())).thenReturn(true);

		assertThrows(BookStateDetailAlreadyExistException.class, () -> bookStateService.registerState(bookStateRequest));
	}

	@Test
	public void getBookState_ShouldReturnBookStateResponse_WhenStateExists() {
		Integer stateId = 1;
		when(bookStateRepository.findById(stateId)).thenReturn(Optional.of(bookState));

		BookStateResponse result = bookStateService.getBookState(stateId);

		assertEquals(bookState.getDetail(), result.detail());
		verify(bookStateRepository, times(1)).findById(stateId);
	}

	@Test
	public void getBookState_ShouldThrowBookStateIdNotFoundException_WhenStateNotFound() {
		Integer stateId = 1;
		when(bookStateRepository.findById(stateId)).thenReturn(Optional.empty());

		assertThrows(BookStateIdNotFoundException.class, () -> bookStateService.getBookState(stateId));
		verify(bookStateRepository, times(1)).findById(stateId);
	}

	@Test
	public void getAllBooks_ShouldReturnBookStateResponseList_WhenBookStatesExist() {
		when(bookStateRepository.findAll()).thenReturn(List.of(bookState));

		List<BookStateResponse> result = bookStateService.getAllBookStates();

		assertEquals(1, result.size());
		assertEquals(bookState.getDetail(), result.get(0).detail());

		verify(bookStateRepository, times(1)).findAll();
	}

	@Test
	public void getAllBooks_ShouldReturnEmptyList_WhenNoBookStatesExist() {
		when(bookStateRepository.findAll()).thenReturn(List.of());

		List<BookStateResponse> result = bookStateService.getAllBookStates();

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
		ReflectionTestUtils.setField(defaultState, "id", DEFAULT_STATE_ID);

		BookState bookStateToDelete = BookState.builder()
			.detail("삭제할 상태")
			.build();
		ReflectionTestUtils.setField(bookStateToDelete, "id", 2);

		Book book1 = mock(Book.class);
		Book book2 = mock(Book.class);

		when(bookStateRepository.findById(DEFAULT_STATE_ID)).thenReturn(Optional.of(defaultState));
		when(bookStateRepository.findById(2)).thenReturn(Optional.of(bookStateToDelete));
		when(bookRepository.findByStateId(bookStateToDelete)).thenReturn(List.of(book1, book2));

		doNothing().when(book1).updateStateId(defaultState);
		doNothing().when(book2).updateStateId(defaultState);

		bookStateService.deleteState(2);

		verify(book1, times(1)).updateStateId(defaultState);
		verify(book2, times(1)).updateStateId(defaultState);

		verify(bookRepository, times(1)).saveAll(List.of(book1, book2));

		verify(bookStateRepository, times(1)).deleteById(2);
	}

	@Test
	public void deleteState_ShouldThrowBookStateIdNotFoundException_WhenStateNotFound() {
		when(bookStateRepository.findById(1)).thenReturn(Optional.empty());

		assertThrows(BookStateIdNotFoundException.class, () -> bookStateService.deleteState(1));

		verify(bookStateRepository, times(1)).findById(1);
	}

	@Test
	public void deleteState_ShouldThrowException_WhenDefaultStateNotFound() {
		when(bookStateRepository.findById(DEFAULT_STATE_ID)).thenReturn(Optional.empty());

		assertThrows(BookStateIdNotFoundException.class, () -> bookStateService.deleteState(1));
		verify(bookStateRepository, never()).deleteById(anyInt());
	}

	@Test
	public void deleteState_ShouldHandleEmptyBookList_WhenStateHasNoAssociatedBooks() {
		BookState defaultState = BookState.builder()
			.detail("기본 상태")
			.build();
		ReflectionTestUtils.setField(defaultState, "id", DEFAULT_STATE_ID);

		BookState bookStateToDelete = BookState.builder()
			.detail("삭제할 상태")
			.build();
		ReflectionTestUtils.setField(bookStateToDelete, "id", 2);

		when(bookStateRepository.findById(DEFAULT_STATE_ID)).thenReturn(Optional.of(defaultState));
		when(bookStateRepository.findById(2)).thenReturn(Optional.of(bookStateToDelete));

		when(bookRepository.findByStateId(bookStateToDelete)).thenReturn(List.of());

		bookStateService.deleteState(2);

		verify(bookRepository, never()).saveAll(anyList());

		verify(bookStateRepository, times(1)).deleteById(2);
	}

	@Test
	public void deleteState_ShouldThrowException_WhenDeletingDefaultState() {
		BookState defaultState = BookState.builder()
			.detail("기본 상태")
			.build();
		ReflectionTestUtils.setField(defaultState, "id", DEFAULT_STATE_ID);

		when(bookStateRepository.findById(DEFAULT_STATE_ID)).thenReturn(Optional.of(defaultState));

		assertThrows(DefaultStateDeletionException.class, () -> bookStateService.deleteState(DEFAULT_STATE_ID));

		verify(bookStateRepository, never()).deleteById(DEFAULT_STATE_ID);
	}
}
