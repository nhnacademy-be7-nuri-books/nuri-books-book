package shop.nuribooks.books.controller.bookstate;

import java.util.List;

import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.bookstate.BookStateRequest;
import shop.nuribooks.books.dto.bookstate.BookStateResponse;
import shop.nuribooks.books.dto.member.ResponseMessage;
import shop.nuribooks.books.service.bookstate.BookStateService;

@RequestMapping("/api/book-state")
@RequiredArgsConstructor
@RestController
public class BookStateController {

	private final BookStateService bookStateService;

	//TODO : X-USER-ID 필요없을듯하여 추후 삭제 예정
	@Operation(summary = "도서 상태 등록", description = "관리자가 새로운 도서 상태를 등록합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "도서 상태 등록 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "409", description = "도서 상태가 이미 존재함"),
	})
	@PostMapping
	public ResponseEntity<ResponseMessage> registerBookState(
		@RequestHeader("X-USER-ID") String adminId,
		@Valid @RequestBody BookStateRequest bookStateReq) {

		bookStateService.registerState(adminId, bookStateReq);
		ResponseMessage responseMessage = new ResponseMessage(HttpStatus.CREATED.value(), "도서상태 등록 성공");
		return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
	}

	@GetMapping
	public ResponseEntity<ResponseMessage> getBookState() {
		bookStateService.getAllBooks();
		ResponseMessage responseMessage = new ResponseMessage(HttpStatus.OK.value(), "도서상태 조회 성공");
		return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
	}
}
