package shop.nuribooks.books.book.booklike.controller;

import org.springframework.data.domain.Page;
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
import shop.nuribooks.books.book.booklike.dto.BookLikeResponse;
import shop.nuribooks.books.book.booklike.dto.LikeStatusResponse;
import shop.nuribooks.books.book.booklike.service.BookLikeService;
import shop.nuribooks.books.common.annotation.HasRole;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;
import shop.nuribooks.books.member.member.entity.AuthorityType;

@RequestMapping("/api/books/book-likes")
@RequiredArgsConstructor
@RestController
public class BookLikeController {
	private final BookLikeService bookLikeService;

	@Operation(
		summary = "도서 좋아요 추가",
		description = "회원이 특정 도서에 좋아요를 추가합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "좋아요 추가 성공"),
		@ApiResponse(responseCode = "404", description = "도서 또는 회원 정보가 존재하지 않음"),
		@ApiResponse(responseCode = "409", description = "이미 좋아요가 존재함")
	})
	@HasRole(role = AuthorityType.MEMBER)
	@PostMapping("/{book-id}")
	public ResponseEntity<Void> addLike(@PathVariable(name = "book-id") Long bookId) {
		Long memberId = MemberIdContext.getMemberId();
		bookLikeService.addLike(memberId, bookId);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Operation(
		summary = "도서 좋아요 삭제",
		description = "회원이 특정 도서에 추가한 좋아요를 삭제합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "좋아요 삭제 성공"),
		@ApiResponse(responseCode = "404", description = "좋아요 또는 도서 정보가 존재하지 않음")
	})
	@HasRole(role = AuthorityType.MEMBER)
	@DeleteMapping("/{book-id}")
	public ResponseEntity<Void> removeLike(@PathVariable(name = "book-id") Long bookId) {
		Long memberId = MemberIdContext.getMemberId();
		bookLikeService.removeLike(memberId, bookId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(
		summary = "회원이 좋아요를 누른 도서 목록 조회",
		description = "특정 회원이 좋아요를 누른 도서들의 목록을 페이지네이션 형태로 조회합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "도서 좋아요 목록 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "404", description = "회원 정보가 존재하지 않음")
	})
	@HasRole(role = AuthorityType.MEMBER)
	@GetMapping("/me")
	public ResponseEntity<Page<BookLikeResponse>> getBookLikes(Pageable pageable) {
		Long memberId = MemberIdContext.getMemberId();
		Page<BookLikeResponse> bookLikes = bookLikeService.getLikedBooks(memberId, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(bookLikes);
	}

	@Operation(
		summary = "도서 좋아요 상태 조회",
		description = "회원이 특정 도서에 좋아요를 눌렀는지 여부를 조회합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "좋아요 상태 조회 성공"),
		@ApiResponse(responseCode = "404", description = "도서 또는 회원 정보가 존재하지 않음")
	})
	@GetMapping("/status/{book-id}")
	public ResponseEntity<LikeStatusResponse> getLikeStatus(@PathVariable(name = "book-id") Long bookId) {
		Long memberId = MemberIdContext.getMemberId();
		boolean isLiked = bookLikeService.isBookLikedByMember(memberId, bookId);
		return ResponseEntity.ok(new LikeStatusResponse(isLiked));
	}
}
