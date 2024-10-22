package shop.nuribooks.books.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.controller.book.BookStateController;
import shop.nuribooks.books.dto.book.request.BookStateReq;
import shop.nuribooks.books.dto.common.ResponseMessage;
import shop.nuribooks.books.entity.book.enums.BookStatesEnum;
import shop.nuribooks.books.exception.book.IsAlreadyExistsBookStateException;
import shop.nuribooks.books.service.book.BookStateService;

@WebMvcTest(BookStateController.class)
public class BookStateControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookStateService bookStateService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void registerBookState_ShouldReturnCreated_WhenRequestIsValid() throws Exception {
		BookStateReq bookStateReq = new BookStateReq();
		bookStateReq.setDetail(BookStatesEnum.INSTOCK);

		ResponseMessage responseMessage = new ResponseMessage(201, "도서상태 등록 성공");

		when(bookStateService.registerState(any(BookStateReq.class))).thenReturn(responseMessage);

		mockMvc.perform(post("/api/book-states")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bookStateReq)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.code").value(201))
			.andExpect(jsonPath("$.message").value("도서상태 등록 성공"));
	}

	@Test
	public void registerBookState_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
		BookStateReq bookStateReq = new BookStateReq();
		bookStateReq.setDetail(null);

		mockMvc.perform(post("/api/book-states")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bookStateReq)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(400))
			.andExpect(jsonPath("$.message").value("도서상태명이 입력되지 않았습니다."));
	}

	@Test
	public void registerBookState_ShouldReturnConflict_WhenStateAlreadyExists() throws Exception {
		BookStateReq bookStateReq = new BookStateReq();
		bookStateReq.setDetail(BookStatesEnum.INSTOCK);

		when(bookStateService.registerState(any(BookStateReq.class)))
			.thenThrow(new IsAlreadyExistsBookStateException(BookStatesEnum.INSTOCK));

		mockMvc.perform(post("/api/book-states")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bookStateReq)))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.statusCode").value(409))
			.andExpect(jsonPath("$.message").value("입력한 isbn 재고있음(INSTOCK) 은 이미 존재합니다."));
	}
}
