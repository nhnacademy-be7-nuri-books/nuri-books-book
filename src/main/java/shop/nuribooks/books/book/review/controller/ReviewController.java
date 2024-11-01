package shop.nuribooks.books.book.review.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.review.dto.request.ReviewRegisterRequest;
import shop.nuribooks.books.book.review.dto.response.ReviewBriefResponse;
import shop.nuribooks.books.book.review.service.ReviewService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {
	private final ReviewService reviewService;

	@Operation(summary = "리뷰 등록", description = "새로운 리뷰를 등록합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "리뷰 생성 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	})
	@PostMapping
	public ResponseEntity<ReviewBriefResponse> registerReview(
		@Valid @RequestBody ReviewRegisterRequest reviewRegisterRequest,
		@RequestHeader("X-USER-ID") long memberId
	) {
		ReviewBriefResponse response = this.reviewService.registerReview(reviewRegisterRequest, memberId);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
