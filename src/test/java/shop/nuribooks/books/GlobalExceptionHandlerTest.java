package shop.nuribooks.books;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import shop.nuribooks.books.common.advice.GlobalExceptionHandler;

@WebMvcTest(controllers = {GlobalExceptionHandler.class})
class GlobalExceptionHandlerTest {

	@Autowired
	private MockMvc mockMvc;
	//
	// // TODO 존재하지 않는 데이터를 조회하는 것을 /nonexistent-endpoint 에 집어 넣기 ex(/books/999)
	// @Test
	// void handleResourceNotFoundException_shouldReturnNotFound() throws Exception {
	// 	mockMvc.perform(get("/nonexistent-endpoint"))
	// 		.andExpect(status().isNotFound())
	// 		.andExpect(jsonPath("$.statusCode").value(404))
	// 		.andExpect(jsonPath("$.message").value("Resource not found"));
	// }
	//
	// // TODO 잘못된 요청을 /invalid-data 에 집어 넣기 ex(책 이름 없이 요청 보내보기)
	// @Test
	// void handleInvalidDataException_shouldReturnBadRequest() throws Exception {
	// 	mockMvc.perform(get("/invalid-data"))
	// 		.andExpect(status().isBadRequest())
	// 		.andExpect(jsonPath("$.statusCode").value(400))
	// 		.andExpect(jsonPath("$.message").value("Invalid data provided"));
	// }

	// @Test
	// void handleGlobalException_shouldReturnInternalServerError() throws Exception {
	// 	mockMvc.perform(get("/internal-error"))
	// 		.andExpect(status().isInternalServerError())
	// 		.andExpect(jsonPath("$.statusCode").value(500))
	// 		.andExpect(jsonPath("$.message").value("An unexpected error occurred"));
	// }
}


