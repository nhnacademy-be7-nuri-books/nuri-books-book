package shop.nuribooks.books.book.booklike.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.booklike.dto.BookLikeResponse;
import shop.nuribooks.books.book.booklike.service.BookLikeService;
import shop.nuribooks.books.common.message.PagedResponse;

@RequestMapping("/api/books")
@RequiredArgsConstructor
@RestController
public class BookLikeController {
	private final BookLikeService bookLikeService;

	@Operation(
		summary = "회원이 좋아요를 누른 도서 목록 조회",
		description = "특정 회원이 좋아요를 누른 도서들의 목록을 페이지네이션 형태로 조회합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "도서 좋아요 목록 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "404", description = "회원 정보가 존재하지 않음")
	})
	@GetMapping("/{member-id}/book-likes")
	public ResponseEntity<PagedResponse<BookLikeResponse>> getBookLikes(@PathVariable("member-id") long memberId,
		Pageable pageable) {
		PagedResponse<BookLikeResponse> bookLikes = bookLikeService.getLikedBooks(memberId, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(bookLikes);
	}

}
