package shop.nuribooks.books.controller.book;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.book.request.BookStateReq;
import shop.nuribooks.books.dto.common.ResponseMessage;
import shop.nuribooks.books.service.book.BookStateService;

@RequestMapping("/api/book-states")
@RequiredArgsConstructor
@RestController
public class BookStateController {

	private final BookStateService bookStateService;

	@Operation(summary = "도서 상태 등록", description = "관리자가 도서 상태를 등록할 수 있는 엔드포인트입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "도서상태 등록 성공"),
		@ApiResponse(responseCode = "400", description = "도서상태명이 입력되지 않았습니다."),
		@ApiResponse(responseCode = "409", description = "이미 존재하는 도서 상태입니다.")
	})
	@PostMapping
	public ResponseEntity<ResponseMessage> registerBookState(@Valid @RequestBody BookStateReq bookStateReq,  BindingResult bindingResult) {
		if(bindingResult.hasErrors()){
			ResponseMessage responseMessage = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), "도서상태명이 입력되지 않았습니다.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMessage);
		}
		ResponseMessage responseMessage = bookStateService.registerState(bookStateReq);
		return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
	}
}
