package shop.nuribooks.books.book.booktag.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.booktag.dto.BookTagGetResponse;
import shop.nuribooks.books.book.booktag.dto.BookTagRegisterResponse;
import shop.nuribooks.books.book.booktag.dto.BookTagRequest;
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
	public ResponseEntity<BookTagRegisterResponse> registerTagToBook(@Valid @RequestBody BookTagRequest request) {
		BookTagRegisterResponse response = bookTagService.registerTagToBook(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/**
	 * 도서에 달린 태그를 조회하는 controller
	 *
	 * @param bookId 조회할 도서의 ID
	 * @return 도서와 관련된 태그 정보, 상태코드 200 포함한 응답 객체 BookTagGetResponse
	 */
	@Operation(summary = "도서 태그 조회", description = "특정 도서에 등록된 태그 목록을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "태그 조회 성공"),
		@ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음")
	})
	@GetMapping("/book/{bookId}")
	public ResponseEntity<BookTagGetResponse> getBookTag(@Valid @PathVariable Long bookId) {
		BookTagGetResponse response = bookTagService.getBookTag(bookId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 태그에 해당하는 모든 도서들을 조회하는 controller
	 *
	 * @param tagId 조회할 태그의 ID
	 * @return 도서들의 정보, 상태코드 200 포함한 응답 객체 BookResponse
	 */
	@Operation(summary = "태그에 해당하는 도서 조회", description = "특정 태그에 속한 모든 도서 목록을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "도서 조회 성공"),
		@ApiResponse(responseCode = "404", description = "태그를 찾을 수 없음")
	})
	@GetMapping("/tag/{tagId}")
	public ResponseEntity<List<BookResponse>> getBooksByTagId(@Valid @PathVariable Long tagId) {
		List<BookResponse> responses = bookTagService.getBooksByTagId(tagId);
		return ResponseEntity.status(HttpStatus.OK).body(responses);
	}

	/**
	 * 도서태그 삭제하는 controller
	 * @param bookTagId 삭제할 도서태그의 ID
	 * @return 상태코드 204 포함한 응답 ResponseEntity
	 */
	@Operation(summary = "도서 태그 삭제", description = "특정 도서 태그를 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "도서 태그 삭제에 성공하였습니다."),
		@ApiResponse(responseCode = "404", description = "해당 도서 태그를 찾을 수 없습니다.")
	})
	@DeleteMapping("/{bookTagId}")
	public ResponseEntity<HttpStatus> deleteBookTag(@Valid @PathVariable Long bookTagId) {
		bookTagService.deleteBookTag(bookTagId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
