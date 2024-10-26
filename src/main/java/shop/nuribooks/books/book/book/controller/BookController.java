package shop.nuribooks.books.book.book.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import shop.nuribooks.books.book.book.dto.BookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BookRegisterResponse;
import shop.nuribooks.books.book.book.dto.BookUpdateRequest;
import shop.nuribooks.books.book.book.service.BookService;
import shop.nuribooks.books.common.message.ResponseMessage;

@RequestMapping("/api/books")
@RequiredArgsConstructor
@RestController
public class BookController {
	private final BookService bookService;

	@Operation(summary = "Register a new book", description = "This endpoint allows administrators to register a new book in the system.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Book registration successful"),
		@ApiResponse(responseCode = "400", description = "Invalid request data"),
		@ApiResponse(responseCode = "404", description = "Book state or publisher not found"),
		@ApiResponse(responseCode = "409", description = "ISBN already exists"),
	})
	@PostMapping
	public ResponseEntity<BookRegisterResponse> registerBooks(@Valid @RequestBody BookRegisterRequest reqDto) {
		BookRegisterResponse resDto = bookService.registerBook(reqDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(resDto);
	}

	@PutMapping("/{bookId}")
	public ResponseEntity<ResponseMessage> updateBook(@PathVariable Long bookId,
		@Valid @RequestBody BookUpdateRequest bookUpdateReq) {
		bookService.updateBook(bookId, bookUpdateReq);
		ResponseMessage responseMessage = new ResponseMessage(HttpStatus.OK.value(), "도서 수정 성공");
		return ResponseEntity.ok(responseMessage);
	}
}
