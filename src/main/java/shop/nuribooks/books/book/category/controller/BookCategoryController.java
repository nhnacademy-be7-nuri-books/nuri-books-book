package shop.nuribooks.books.book.category.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.category.service.BookCategoryService;

@RequiredArgsConstructor
@RequestMapping("/api/book-category")
@RestController
public class BookCategoryController {

	private final BookCategoryService bookCategoryService;

	/**
	 * 도서와 카테고리를 연관시킵니다.
	 *
	 * @param bookId     연관시킬 도서의 ID
	 * @param categoryId 연관시킬 카테고리의 ID
	 * @return ResponseEntity<Void>
	 */
	@Operation(summary = "도서와 카테고리를 연관시킵니다.", description = "도서 ID와 카테고리 ID를 받아 두 객체를 연관시킵니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "연관 관계가 성공적으로 생성됨"),
		@ApiResponse(responseCode = "404", description = "도서 또는 카테고리를 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@PostMapping("/{bookId}/categories/{categoryId}")
	public ResponseEntity<Void> registerBookCategory(
		@PathVariable Long bookId,
		@PathVariable Long categoryId) {
		bookCategoryService.registerBookCategory(bookId, categoryId);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
