package shop.nuribooks.books.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import shop.nuribooks.books.dto.book.request.BookStateReq;
import shop.nuribooks.books.dto.common.ResponseMessage;
import shop.nuribooks.books.entity.book.BookStates;
import shop.nuribooks.books.entity.book.enums.BookStatesEnum;
import shop.nuribooks.books.exception.BadRequestException;
import shop.nuribooks.books.exception.book.IsAlreadyExistsBookStateException;
import shop.nuribooks.books.repository.book.BookStateRepository;
import shop.nuribooks.books.service.book.impl.BookStateServiceImpl;

@ExtendWith(MockitoExtension.class)
public class BookStateServiceImplTest {

	@InjectMocks
	private BookStateServiceImpl bookStateService;

	@Mock
	private BookStateRepository bookStateRepository;

	private BookStateReq bookStateReq;

	@BeforeEach
	public void setUp() {
		bookStateReq = new BookStateReq();
		bookStateReq.setDetail(BookStatesEnum.INSTOCK);
	}

	@Test
	public void registerState_ShouldSaveBookState_WhenRequestIsValid() {
		when(bookStateRepository.existsBookStatesByDetail(bookStateReq.getDetail())).thenReturn(false);

		ResponseMessage response = bookStateService.registerState(bookStateReq);

		assertEquals(HttpStatus.CREATED.value(), response.getCode());
		assertEquals("도서상태 등록 성공", response.getMessage());
		verify(bookStateRepository, times(1)).save(any(BookStates.class));
		verify(bookStateRepository, times(1)).existsBookStatesByDetail(bookStateReq.getDetail());
	}

	@Test
	public void registerState_ShouldThrowException_WhenRequestIsNull() {
		BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
			bookStateService.registerState(null);
		});
		assertEquals("요청 본문이 비어있습니다.", thrown.getMessage());
	}

	@Test
	public void registerState_ShouldThrowException_WhenStateAlreadyExists() {
		when(bookStateRepository.existsBookStatesByDetail(bookStateReq.getDetail())).thenReturn(true);

		IsAlreadyExistsBookStateException thrown = assertThrows(IsAlreadyExistsBookStateException.class, () -> {
			bookStateService.registerState(bookStateReq);
		});
		assertTrue(thrown.getMessage().contains(bookStateReq.getDetail().toString()));
	}
}
