package shop.nuribooks.books.book.bookstate.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.book.bookstate.dto.BookStateRequest;
import shop.nuribooks.books.book.bookstate.dto.BookStateResponse;
import shop.nuribooks.books.exception.ResourceAlreadyExistException;
import shop.nuribooks.books.exception.bookstate.BookStateIdNotFoundException;
import shop.nuribooks.books.book.bookstate.service.BookStateService;

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
		String adminId = "admin123";
		BookStateRequest bookStateReq = new BookStateRequest("재고있음");

		doNothing().when(bookStateService).registerState(eq(adminId), any(BookStateRequest.class));

		mockMvc.perform(post("/api/book-state")
				.header("X-USER-ID", adminId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bookStateReq)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.statusCode").value(201))
			.andExpect(jsonPath("$.message").value("도서상태 등록 성공"));
	}

	@Test
	public void registerBookState_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
		String adminId = "admin123";
		BookStateRequest bookStateReq = new BookStateRequest(null);

		mockMvc.perform(post("/api/book-state")
				.header("X-USER-ID", adminId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bookStateReq)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.statusCode").value(400))
			.andExpect(jsonPath("$.message").value("도서상태명은 필수입니다."));
	}

	@Test
	public void registerBookState_ShouldReturnConflict_WhenStateAlreadyExists() throws Exception {
		String adminId = "admin123"; // 예시 adminId
		BookStateRequest bookStateReq = new BookStateRequest("재고있음");

		doThrow(new ResourceAlreadyExistException("입력한 도서상태명 " + bookStateReq.detail() + " 이 이미 존재합니다."))
			.when(bookStateService).registerState(eq(adminId), any(BookStateRequest.class));

		mockMvc.perform(post("/api/book-state")
				.header("X-USER-ID", adminId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bookStateReq)))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.statusCode").value(409))
			.andExpect(jsonPath("$.message").value("입력한 도서상태명 재고있음 이 이미 존재합니다."));
	}

	@Test
	public void getBookState_ShouldReturnOk_WhenStatesExist() throws Exception {
		// 예시 도서 상태 목록
		List<BookStateResponse> bookStates = List.of(
			new BookStateResponse(1, "재고있음"),
			new BookStateResponse(2, "재입고"),
			new BookStateResponse(3, "매진")
		);

		when(bookStateService.getAllBooks()).thenReturn(bookStates);

		mockMvc.perform(get("/api/book-state"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(bookStates.size()))
			.andExpect(jsonPath("$[0].id").value(1))
			.andExpect(jsonPath("$[0].detail").value("재고있음"));
	}

	@Test
	public void getBookState_ShouldReturnEmptyList_WhenNoStatesExist() throws Exception {
		when(bookStateService.getAllBooks()).thenReturn(List.of());

		mockMvc.perform(get("/api/book-state"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(0));
	}

	@Test
	public void updateBookState_ShouldReturnOk_WhenRequestIsValid() throws Exception {
		Integer stateId = 1;
		BookStateRequest bookStateReq = new BookStateRequest("재입고");

		doNothing().when(bookStateService).updateState(eq(stateId), any(BookStateRequest.class));

		mockMvc.perform(patch("/api/book-state/{id}", stateId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bookStateReq)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.statusCode").value(200))
			.andExpect(jsonPath("$.message").value("도서상태 수정 성공"));
	}

	@Test
	public void updateBookState_ShouldReturnNotFound_WhenStateDoesNotExist() throws Exception {
		Integer stateId = 1;
		BookStateRequest bookStateReq = new BookStateRequest("재입고");

		doThrow(new BookStateIdNotFoundException())
			.when(bookStateService).updateState(eq(stateId), any(BookStateRequest.class));

		mockMvc.perform(patch("/api/book-state/{id}", stateId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bookStateReq)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.statusCode").value(404))
			.andExpect(jsonPath("$.message").value("도서상태를 찾을 수 없습니다."));
	}

	@Test
	public void deleteBookState_ShouldReturnOk_WhenStateIsDeleted() throws Exception {
		Integer stateId = 1;

		doNothing().when(bookStateService).deleteState(stateId);

		mockMvc.perform(delete("/api/book-state/{id}", stateId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.statusCode").value(200))
			.andExpect(jsonPath("$.message").value("도서상태 삭제 성공"));
	}

	@Test
	public void deleteBookState_ShouldReturnNotFound_WhenStateDoesNotExist() throws Exception {
		Integer stateId = 1;

		doThrow(new BookStateIdNotFoundException())
			.when(bookStateService).deleteState(stateId);

		mockMvc.perform(delete("/api/book-state/{id}", stateId))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.statusCode").value(404))
			.andExpect(jsonPath("$.message").value("도서상태를 찾을 수 없습니다."));
	}
}
