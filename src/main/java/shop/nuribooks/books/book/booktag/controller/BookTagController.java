package shop.nuribooks.books.book.booktag.controller;

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
import shop.nuribooks.books.book.booktag.dto.BookTagRequest;
import shop.nuribooks.books.book.booktag.dto.BookTagResponse;
import shop.nuribooks.books.book.booktag.service.BookTagService;

@RequiredArgsConstructor
@RequestMapping("/api/book-tags")
@RestController
public class BookTagController {
	private final BookTagService bookTagService;

	/**
	 * 도서에 태그를 등록하는 controller
	 *
	 * @param request 등록할 태그 정보가 포함된 요청 객체
	 * @return 등록된 도서와 태그의 정보, 상태코드 201 포함한 응답 객체 BookTagResponse
	 */
	@Operation(summary = "책에 태그 등록", description = "지정된 책에 태그를 등록합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "태그 등록 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 형식")
	})
	@PostMapping
	public ResponseEntity<BookTagResponse> registerTagToBook(@Valid @RequestBody BookTagRequest request) {
		BookTagResponse response = bookTagService.registerTagToBook(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
