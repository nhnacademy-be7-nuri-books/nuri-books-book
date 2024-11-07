package shop.nuribooks.books.book.review.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.review.dto.request.ReviewRequest;
import shop.nuribooks.books.book.review.dto.response.ReviewBookResponse;
import shop.nuribooks.books.book.review.dto.response.ReviewMemberResponse;
import shop.nuribooks.books.book.review.service.ReviewService;

@RestController
@RequiredArgsConstructor
@RequestMapping()
public class ReviewController {
	private final ReviewService reviewService;

	@Operation(summary = "리뷰 등록", description = "새로운 리뷰를 등록합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "리뷰 생성 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	})
	@PostMapping("/api/reviews")
	public ResponseEntity<ReviewMemberResponse> registerReview(
		@Valid @RequestBody ReviewRequest reviewRequest,
		@RequestHeader("X-USER-ID") long ownerId
	) {
		ReviewMemberResponse response = this.reviewService.registerReview(reviewRequest, ownerId);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "도서 상세 리뷰 조회", description = "도서에 관련된 리뷰를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "리뷰 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	})
	@GetMapping("/api/books/{bookId}/reviews")
	public ResponseEntity<List<ReviewMemberResponse>> getReviewMember(
		@PathVariable("bookId") long bookId
	) {
		List<ReviewMemberResponse> response = this.reviewService.getReviewsWithMember(bookId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "유저 상세 리뷰 조회", description = "유저에 관련된 리뷰를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "리뷰 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	})
	@GetMapping("/api/members/{memberId}/reviews")
	public ResponseEntity<List<ReviewBookResponse>> getReviewBook(
		@PathVariable("memberId") long memberId
	) {
		List<ReviewBookResponse> response = this.reviewService.getReviewsWithBook(memberId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "리뷰 업데이트", description = "리뷰를 업데이트합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "리뷰 업데이트 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "404", description = "id 발견 못함"),
	})
	@PutMapping("/api/reviews/{reviewId}")
	public ResponseEntity<ReviewMemberResponse> updateReview(
		@Valid @RequestBody ReviewRequest reviewRequest,
		@PathVariable long reviewId,
		@RequestHeader("X-USER-ID") long ownerId
	) {
		ReviewMemberResponse response = this.reviewService.updateReview(reviewRequest, reviewId, ownerId);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
