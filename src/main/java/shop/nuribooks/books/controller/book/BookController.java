package shop.nuribooks.books.controller.book;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.book.BookRegisterRequest;
import shop.nuribooks.books.dto.book.BookRegisterResponse;
import shop.nuribooks.books.service.book.BookService;

@RequestMapping("/api/books")
@RequiredArgsConstructor
@RestController
public class BookController {
	private final BookService booksService;

	@Operation(summary = "Register a new book", description = "This endpoint allows administrators to register a new book in the system.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Book registration successful"),
		@ApiResponse(responseCode = "400", description = "Invalid request data"),
		@ApiResponse(responseCode = "404", description = "Book state or publisher not found"),
		@ApiResponse(responseCode = "409", description = "ISBN already exists"),
	})
	@PostMapping
	public ResponseEntity<BookRegisterResponse> registerBooks(@Valid @RequestBody BookRegisterRequest reqDto) {
		BookRegisterResponse resDto = booksService.registerBook(reqDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(resDto);
	}
}
