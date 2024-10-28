package shop.nuribooks.books.book.bookstate.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import shop.nuribooks.books.book.bookstate.dto.BookStateRequest;
import shop.nuribooks.books.book.bookstate.dto.BookStateResponse;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.book.bookstate.service.BookStateService;

@RequestMapping("/api/book-state")
@RequiredArgsConstructor
@RestController
public class BookStateController {

	private final BookStateService bookStateService;

	@Operation(summary = "도서 상태 등록", description = "관리자가 새로운 도서 상태를 등록합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "도서 상태 등록 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "409", description = "도서 상태가 이미 존재함"),
	})
	@PostMapping
	public ResponseEntity<ResponseMessage> registerBookState(@Valid @RequestBody BookStateRequest bookStateReq) {
		bookStateService.registerState(bookStateReq);
		ResponseMessage responseMessage = new ResponseMessage(HttpStatus.CREATED.value(), "도서상태 등록 성공");
		return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
	}

	@Operation(summary = "도서 상태 조회", description = "모든 도서 상태를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "도서 상태 조회 성공"),
	})
	@GetMapping
	public ResponseEntity<List<BookStateResponse>> getBookState() {
		List<BookStateResponse> bookStateResponses = bookStateService.getAllBookStates();
		return ResponseEntity.ok(bookStateResponses);
	}

	@PatchMapping("/{id}")
	@Operation(summary = "도서 상태 수정", description = "관리자가 도서 상태를 수정합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "도서 상태 수정 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "404", description = "도서 상태를 찾을 수 없음"),
		@ApiResponse(responseCode = "409", description = "도서 상태가 이미 존재함"),
	})
	public ResponseEntity<ResponseMessage> updateBookState(
		@PathVariable Integer id,
		@Valid @RequestBody BookStateRequest bookStateReq) {

		bookStateService.updateState(id, bookStateReq);
		ResponseMessage responseMessage = new ResponseMessage(HttpStatus.OK.value(), "도서상태 수정 성공");
		return ResponseEntity.ok(responseMessage);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "도서 상태 삭제", description = "관리자가 도서 상태를 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "도서 상태 삭제 성공"),
		@ApiResponse(responseCode = "404", description = "도서 상태를 찾을 수 없음"),
	})
	public ResponseEntity<Void> deleteBookState(@PathVariable Integer id) {
		bookStateService.deleteState(id);
		return ResponseEntity.noContent().build();
	}
}
