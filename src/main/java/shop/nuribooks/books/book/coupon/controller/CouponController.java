package shop.nuribooks.books.book.coupon.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.coupon.dto.BookCouponResponse;
import shop.nuribooks.books.book.coupon.dto.CouponRequest;
import shop.nuribooks.books.book.coupon.dto.CouponResponse;
import shop.nuribooks.books.book.coupon.enums.CouponType;
import shop.nuribooks.books.book.coupon.service.CouponService;
import shop.nuribooks.books.common.annotation.HasRole;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.member.member.entity.AuthorityType;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponController {
	private final CouponService couponService;

	@Operation(summary = "쿠폰 생성", description = "쿠폰을 생성합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "생성 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	})
	@HasRole(role = AuthorityType.ADMIN)
	@PostMapping
	public ResponseEntity<ResponseMessage> registerCoupon(
		@Valid @RequestBody CouponRequest couponRequest) {
		couponService.registerCoupon(couponRequest);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new ResponseMessage(HttpStatus.CREATED.value(), "쿠폰 생성 성공"));
	}

	@Operation(summary = "쿠폰 타입별 목록 조회", description = "쿠폰 타입별 목록을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	})
	@HasRole(role = AuthorityType.ADMIN)
	@GetMapping
	public ResponseEntity<Page<CouponResponse>> getCoupons(@RequestParam CouponType type, Pageable pageable) {
		Page<CouponResponse> couponPolicyResponses = couponService.getCoupons(type, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(couponPolicyResponses);
	}

	@Operation(summary = "쿠폰 전체 목록 조회", description = "쿠폰 전체 목록을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	})
	@HasRole(role = AuthorityType.ADMIN)
	@GetMapping("/list")
	public ResponseEntity<Page<CouponResponse>> getCoupons(Pageable pageable) {
		Page<CouponResponse> couponPolicyResponses = couponService.getAllCoupons(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(couponPolicyResponses);
	}

	@Operation(summary = "쿠폰 조회", description = "쿠폰을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	})
	@HasRole(role = AuthorityType.ADMIN)
	@GetMapping("/{coupon-id}")
	public ResponseEntity<CouponResponse> getCoupon(@PathVariable(name = "coupon-id") Long id) {
		CouponResponse coupon = couponService.getCouponById(id);
		return ResponseEntity.status(HttpStatus.OK).body(coupon);
	}

	@Operation(summary = "쿠폰 업데이트", description = "쿠폰 정보를 업데이트합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "업데이트 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	})
	@HasRole(role = AuthorityType.ADMIN)
	@PutMapping("/{coupon-id}")
	public ResponseEntity<ResponseMessage> updateCoupon(@PathVariable("coupon-id") Long id,
		@Valid @RequestBody CouponRequest couponRequest) {
		couponService.updateCoupon(id, couponRequest);
		return ResponseEntity.status(HttpStatus.OK)
			.body(new ResponseMessage(HttpStatus.OK.value(), "쿠폰 업데이트 성공"));
	}

	@Operation(summary = "쿠폰 삭제", description = "쿠폰을 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "삭제 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	})
	@HasRole(role = AuthorityType.ADMIN)
	@PutMapping("/{coupon-id}/expire")
	public ResponseEntity<ResponseMessage> expireCoupon(@PathVariable("coupon-id") Long id) {
		couponService.expireCoupon(id);
		return ResponseEntity.status(HttpStatus.OK)
			.body(new ResponseMessage(HttpStatus.OK.value(), "쿠폰 삭제 성공"));
	}

	@GetMapping("/book-coupons/{book-id}")
	public ResponseEntity<BookCouponResponse> getBookCoupon(@PathVariable(name = "book-id") Long bookId) {
		BookCouponResponse response = couponService.getBookCoupon(bookId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
