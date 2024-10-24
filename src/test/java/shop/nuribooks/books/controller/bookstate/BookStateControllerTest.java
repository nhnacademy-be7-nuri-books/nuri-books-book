package shop.nuribooks.books.controller.bookstate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.dto.bookstate.BookStateRequest;
import shop.nuribooks.books.entity.book.BookStateEnum;
import shop.nuribooks.books.exception.ResourceAlreadyExistException;
import shop.nuribooks.books.service.bookstate.BookStateService;

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
		String adminId = "admin123"; // 예시 adminId
		BookStateRequest bookStateReq = new BookStateRequest(BookStateEnum.INSTOCK);

		doNothing().when(bookStateService).registerState(eq(adminId), any(BookStateRequest.class));

		mockMvc.perform(post("/api/book-state")
				.header("X-USER-ID", adminId) // adminId를 헤더에 추가
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bookStateReq)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.statusCode").value(201))
			.andExpect(jsonPath("$.message").value("도서상태 등록 성공"));
	}

	@Test
	public void registerBookState_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
		String adminId = "admin123"; // 예시 adminId
		BookStateRequest bookStateReq = new BookStateRequest(null);

		mockMvc.perform(post("/api/book-state")
				.header("X-USER-ID", adminId) // adminId를 헤더에 추가
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bookStateReq)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.statusCode").value(400))
			.andExpect(jsonPath("$.message").value("도서상태명은 필수입니다."));
	}

	@Test
	public void registerBookState_ShouldReturnConflict_WhenStateAlreadyExists() throws Exception {
		String adminId = "admin123"; // 예시 adminId
		BookStateRequest bookStateReq = new BookStateRequest(BookStateEnum.INSTOCK);

		doThrow(new ResourceAlreadyExistException("입력한 도서상태명 " + bookStateReq.detail() + " 이 이미 존재합니다."))
			.when(bookStateService).registerState(eq(adminId), any(BookStateRequest.class));

		mockMvc.perform(post("/api/book-state")
				.header("X-USER-ID", adminId) // adminId를 헤더에 추가
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bookStateReq)))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.statusCode").value(409))
			.andExpect(jsonPath("$.message").value("입력한 도서상태명 재고있음 이 이미 존재합니다."));
	}
}
