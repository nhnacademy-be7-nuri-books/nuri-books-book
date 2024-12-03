package shop.nuribooks.books.book.review.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.book.review.dto.request.ReviewRequest;
import shop.nuribooks.books.book.review.dto.request.ReviewUpdateRequest;
import shop.nuribooks.books.book.review.dto.response.ReviewBookResponse;
import shop.nuribooks.books.book.review.dto.response.ReviewMemberResponse;
import shop.nuribooks.books.book.review.dto.response.ReviewScoreResponse;
import shop.nuribooks.books.book.review.service.ReviewService;
import shop.nuribooks.books.common.message.PagedResponse;

@Slf4j
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
		@Valid @RequestBody ReviewRequest reviewRequest
	) {
		ReviewMemberResponse response = this.reviewService.registerReview(reviewRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "도서 상세 리뷰 조회", description = "도서에 관련된 리뷰를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "리뷰 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	})
	@GetMapping("/api/books/{bookId}/reviews")
	public ResponseEntity<Page<ReviewMemberResponse>> getReviewMember(
		@PathVariable("bookId") long bookId,
		Pageable pageable
	) {
		Page<ReviewMemberResponse> response = this.reviewService.getReviewsByBookId(bookId, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "유저 상세 리뷰 조회", description = "유저에 관련된 리뷰를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "리뷰 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	})
	@GetMapping("/api/members/{memberId}/reviews")
	public ResponseEntity<PagedResponse<ReviewBookResponse>> getReviewBook(
		@PathVariable("memberId") long memberId,
		Pageable pageable
	) {
		PagedResponse<ReviewBookResponse> response = this.reviewService.getReviewsByMemberId(memberId, pageable);
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
		@Valid @RequestBody ReviewUpdateRequest reviewUpdateRequest,
		@PathVariable long reviewId
	) {
		ReviewMemberResponse response = this.reviewService.updateReview(reviewUpdateRequest, reviewId);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/api/books/{bookId}/score")
	public ResponseEntity<ReviewScoreResponse> getReviewScore(@PathVariable long bookId) {
		ReviewScoreResponse reviewScoreResponse = reviewService.getScoreByBookId(bookId);
		return ResponseEntity.status(HttpStatus.OK).body(reviewScoreResponse);
	}
}
