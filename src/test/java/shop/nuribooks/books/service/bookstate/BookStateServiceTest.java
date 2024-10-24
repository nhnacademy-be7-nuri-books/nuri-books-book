package shop.nuribooks.books.service.bookstate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import shop.nuribooks.books.dto.bookstate.BookStateRequest;
import shop.nuribooks.books.dto.member.ResponseMessage;
import shop.nuribooks.books.entity.book.BookState;
import shop.nuribooks.books.entity.book.BookStateEnum;
import shop.nuribooks.books.exception.ResourceAlreadyExistException;
import shop.nuribooks.books.repository.bookstate.BookStateRepository;
import shop.nuribooks.books.service.bookstate.Impl.BookStateServiceImpl;

@ExtendWith(MockitoExtension.class)
public class BookStateServiceTest {

	@InjectMocks
	private BookStateServiceImpl bookStateService;

	@Mock
	private BookStateRepository bookStateRepository;

	private BookStateRequest bookStateRequest;

	@BeforeEach
	public void setUp() {
		bookStateRequest = new BookStateRequest("재고있음");
	}

	@Test
	public void registerState_ShouldSaveBookState_WhenRequestIsValid() {
		String admin = "admin";

		when(bookStateRepository.existsBookStatesByDetail(bookStateRequest.detail())).thenReturn(false);

		bookStateService.registerState(admin, bookStateRequest);
		ResponseMessage responseMessage = new ResponseMessage(HttpStatus.CREATED.value(), "도서상태 등록 성공");

		assertEquals(HttpStatus.CREATED.value(), responseMessage.getStatusCode());
		assertEquals("도서상태 등록 성공", responseMessage.getMessage());
		verify(bookStateRepository, times(1)).save(any(BookState.class));
		verify(bookStateRepository, times(1)).existsBookStatesByDetail(bookStateRequest.detail());
	}

	@Test
	public void registerState_ShouldThrowException_WhenStateAlreadyExists() {
		String admin = "admin";

		when(bookStateRepository.existsBookStatesByDetail(bookStateRequest.detail())).thenReturn(true);

		ResourceAlreadyExistException thrown = assertThrows(ResourceAlreadyExistException.class, () -> {
			bookStateService.registerState(admin, bookStateRequest);
		});
		assertTrue(thrown.getMessage().contains(bookStateRequest.detail()));
	}
}
