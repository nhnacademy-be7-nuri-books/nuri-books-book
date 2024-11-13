package shop.nuribooks.books.book.coupon.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

	@Operation(summary = "쿠폰 정책 업데이트", description = "포인터 정책을 업데이트합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "업데이트 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	})
	@HasRole(role = AuthorityType.ADMIN)
	@PutMapping("/{coupon-policy-id}")
	public ResponseEntity<ResponseMessage> updateCouponPolicy(@PathVariable("coupon-policy-id") Long id,
		@Valid @RequestBody CouponPolicyRequest couponPolicyRequest) {
		this.couponPolicyService.updateCouponPolicy(id, couponPolicyRequest);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new ResponseMessage(HttpStatus.CREATED.value(), "쿠폰 정책 업데이트 성공"));
	}


	@Operation(summary = "쿠폰 정책 삭제", description = "쿠폰 정책을 삭제합니다폰.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "삭제 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	})
	@HasRole(role = AuthorityType.ADMIN)
	@DeleteMapping("/{coupon-policy-id}")
	public ResponseEntity<ResponseMessage> deleteCouponPolicy(@PathVariable("coupon-policy-id") Long id) {
		couponPolicyService.deleteCouponPolicy(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT)
			.body(new ResponseMessage(HttpStatus.NO_CONTENT.value(), "쿠폰 정책 삭제 성공"));
	}
}
