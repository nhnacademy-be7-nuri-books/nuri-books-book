package shop.nuribooks.books.book.coupon.controller;

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
import shop.nuribooks.books.book.coupon.dto.CouponPolicyRequest;
import shop.nuribooks.books.book.coupon.service.CouponPolicyService;
import shop.nuribooks.books.common.annotation.HasRole;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.member.member.entity.AuthorityType;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupon-policies")
public class CouponPolicyController {
	private final CouponPolicyService couponPolicyService;

	@Operation(summary = "쿠폰 정책 생성", description = "쿠폰 정책을 생성합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "생성 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	})
	@HasRole(role = AuthorityType.ADMIN)
	@PostMapping
	public ResponseEntity<ResponseMessage> registerCouponPolicy(
		@Valid @RequestBody CouponPolicyRequest couponPolicyRequest) {
		couponPolicyService.registerCouponPolicy(couponPolicyRequest);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new ResponseMessage(HttpStatus.CREATED.value(), "쿠폰 정책 생성 성공"));
	}
}
