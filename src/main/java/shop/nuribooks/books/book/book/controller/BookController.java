package shop.nuribooks.books.book.book.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.dto.AdminBookListResponse;
import shop.nuribooks.books.book.book.dto.BookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BookRegisterResponse;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.dto.BookUpdateRequest;
import shop.nuribooks.books.book.book.service.BookService;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.exception.InvalidPageRequestException;

@RequestMapping("/api/books")
@RequiredArgsConstructor
@RestController
public class BookController {
	private final BookService bookService;

	@Operation(summary = "신규 도서 등록", description = "관리자가 새로운 도서를 시스템에 등록할 수 있는 엔드포인트입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "도서 등록 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "404", description = "도서 상태 또는 출판사 미발견"),
		@ApiResponse(responseCode = "409", description = "중복된 ISBN"),
	})
	@PostMapping
	public ResponseEntity<BookRegisterResponse> registerBooks(@Valid @RequestBody BookRegisterRequest reqDto) {
		BookRegisterResponse resDto = bookService.registerBook(reqDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(resDto);
	}

	@Operation(summary = "도서 목록 조회", description = "페이지네이션을 통해 도서 목록을 조회하는 엔드포인트입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "도서 목록 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 페이징 요청")
	})
	@GetMapping
	public ResponseEntity<Page<AdminBookListResponse>> getBooks(Pageable pageable) {
		Page<AdminBookListResponse> adminBookListResponse = bookService.getBooks(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(adminBookListResponse);
	}

	/*@Operation(summary = "도서 상세 조회", description = "도서 ID를 통해 도서의 상세 정보를 조회하는 엔드포인트입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "도서 조회 성공"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 도서입니다.")
	})
	@GetMapping("/{bookId}")
	public ResponseEntity<BookResponse> getBookById(@PathVariable Long bookId) {
		BookResponse bookResponse = bookService.getBookById(bookId);
		return ResponseEntity.status(HttpStatus.OK).body(bookResponse);
	}*/

	@Operation(summary = "도서 정보 수정", description = "관리자가 도서 ID를 통해 기존 도서의 정보를 수정할 수 있는 엔드포인트입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "도서 수정 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "404", description = "도서 미발견")
	})
	@PutMapping("/{bookId}")
	public ResponseEntity<ResponseMessage> updateBook(@PathVariable Long bookId,
		@Valid @RequestBody BookUpdateRequest bookUpdateReq) {

		bookService.updateBook(bookId, bookUpdateReq);
		ResponseMessage responseMessage = new ResponseMessage(HttpStatus.OK.value(), "도서 수정 성공");
		return ResponseEntity.ok(responseMessage);
	}

	@Operation(summary = "도서 삭제", description = "관리자가 도서 ID를 통해 도서를 삭제할 수 있는 엔드포인트입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "도서 삭제 성공"),
		@ApiResponse(responseCode = "404", description = "도서 미발견")
	})
	@DeleteMapping("{bookId}")
	public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
		bookService.deleteBook(bookId);
		return ResponseEntity.noContent().build();
	}
}
