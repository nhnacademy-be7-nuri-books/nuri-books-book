package shop.nuribooks.books.book.book.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.dto.AladinBookListItemResponse;
import shop.nuribooks.books.book.book.dto.AladinBookSaveRequest;
import shop.nuribooks.books.book.book.dto.BaseBookRegisterRequest;
import shop.nuribooks.books.book.book.service.AladinBookService;
import shop.nuribooks.books.common.message.ResponseMessage;

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
	public ResponseEntity<List<AladinBookListItemResponse>> getAladinBookList(@RequestParam(defaultValue = "ItemNewAll") String queryType,
																			@RequestParam(defaultValue = "ItemNewAll") String searchTarget,
																			@RequestParam(defaultValue = "10") int maxResults) {
		List<AladinBookListItemResponse> books = aladinBookService.getNewBooks(queryType, searchTarget, maxResults);
		return ResponseEntity.ok(books);
	}

	//알라딘API를 통해 조회한 도서 리스트에서 선택한 도서의 정보를 가져오기위한 메서드
	@GetMapping("/{isbn}")
	public ResponseEntity<AladinBookListItemResponse> getBookByIsbn(@PathVariable String isbn) {
		AladinBookListItemResponse selectedBook = aladinBookService.getBookByIsbnWithAladin(isbn);
		return ResponseEntity.ok(selectedBook);
	}
}
