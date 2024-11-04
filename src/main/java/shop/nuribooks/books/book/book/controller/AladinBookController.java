package shop.nuribooks.books.book.book.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.dto.AladinBookListItemResponse;
import shop.nuribooks.books.book.book.dto.AladinBookSaveRequest;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.service.AladinBookService;

@RequestMapping("api/books/aladin")
@RequiredArgsConstructor
@RestController
public class AladinBookController {
	private final AladinBookService aladinBookService;

	@Operation(summary = "Retrieve new books from Aladin")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Successful retrieval of new books"),
		@ApiResponse(responseCode = "404", description = "Books not found"),
		@ApiResponse(responseCode = "500", description = "Internal server error")
	})
	@GetMapping
	public ResponseEntity<List<AladinBookListItemResponse>> getAladinBookList() {
		List<AladinBookListItemResponse> books = aladinBookService.getNewBooks();
		return ResponseEntity.ok(books);
	}

	@PostMapping("/save")
	public ResponseEntity<BookResponse> saveAladinBook(@Valid @RequestBody AladinBookSaveRequest aladinBookSaveReq) {
		BookResponse bookResponse = aladinBookService.saveBook(aladinBookSaveReq);
		return ResponseEntity.status(HttpStatus.CREATED).body(bookResponse);
	}
}
