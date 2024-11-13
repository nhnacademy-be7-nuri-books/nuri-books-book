package shop.nuribooks.books.book.category.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.dto.AdminBookListResponse;
import shop.nuribooks.books.book.book.dto.BookContributorsResponse;
import shop.nuribooks.books.book.category.dto.SimpleCategoryResponse;
import shop.nuribooks.books.book.category.service.BookCategoryService;
import shop.nuribooks.books.common.message.PagedResponse;

@RequiredArgsConstructor
//@RequestMapping("/api/book-category")
@RequestMapping("/api/books")
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

	/**
	 * 도서와 카테고리 간의 연관 관계를 삭제합니다.
	 *
	 * @param bookId     삭제할 도서의 ID
	 * @param categoryId 삭제할 카테고리의 ID
	 * @return 성공 시 204 No Content 응답을 반환합니다.
	 */
	@Operation(summary = "도서와 카테고리 연관 관계 삭제", description = "도서 ID와 카테고리 ID를 받아 연관 관계를 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "연관 관계 삭제 성공"),
		@ApiResponse(responseCode = "404", description = "도서, 카테고리 또는 연관 관계를 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@DeleteMapping("/{bookId}/categories/{categoryId}")
	public ResponseEntity<Void> deleteBookCategory(
		@PathVariable Long bookId,
		@PathVariable Long categoryId) {
		bookCategoryService.deleteBookCategory(bookId, categoryId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * 주어진 도서 ID에 해당하는 카테고리 목록을 조회합니다.
	 *
	 * @param bookId 조회할 도서의 ID
	 * @return 도서에 연결된 카테고리 목록을 성공적으로 반환하면 200 OK 응답을 반환합니다.
	 */
	@Operation(summary = "도서의 카테고리 조회", description = "도서 ID를 받아 해당 도서와 연관된 카테고리 목록을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "카테고리 목록 조회 성공"),
		@ApiResponse(responseCode = "404", description = "도서 또는 연관된 카테고리를 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@GetMapping("/book/{bookId}")
	public ResponseEntity<List<List<SimpleCategoryResponse>>> getCategoriesByBookId(
		@PathVariable Long bookId
	) {
		return ResponseEntity.ok(bookCategoryService.findCategoriesByBookId(bookId));
	}

	/**
	 * 주어진 카테고리 ID로 책을 페이지네이션하여 조회합니다.
	 *
	 * @param categoryId 책을 조회할 카테고리의 ID
	 * @param pageable   페이지네이션 정보
	 * @return 책 정보가 포함된 페이지 응답
	 */
	@Operation(summary = "카테고리 ID로 책 조회", description = "주어진 카테고리 ID에 해당하는 책 목록을 페이지네이션하여 반환합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공적으로 책 목록을 반환함"),
		@ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음")
	})
	@GetMapping("/category/{category-id}")
	public ResponseEntity<PagedResponse<BookContributorsResponse>> getBooksByCategoryId(
		@PathVariable(name = "category-id") Long categoryId,
		Pageable pageable) {
		PagedResponse<BookContributorsResponse> pagedResponse = bookCategoryService.findBooksByCategoryId(categoryId,
			pageable);
		return ResponseEntity.ok(pagedResponse);
	}

}
