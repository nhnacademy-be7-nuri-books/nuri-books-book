package shop.nuribooks.books.book.coupon.categorycoupon.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.coupon.categorycoupon.dto.CategoryCouponRequest;
import shop.nuribooks.books.book.coupon.categorycoupon.service.CategoryCouponService;
import shop.nuribooks.books.common.annotation.HasRole;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.member.member.entity.AuthorityType;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons/category-coupons")
public class CategoryCouponController {
	private final CategoryCouponService categoryCouponService;

	@HasRole(role = AuthorityType.ADMIN)
	@PostMapping
	public ResponseEntity<ResponseMessage> registerCategoryCoupon(
		@Valid @RequestBody CategoryCouponRequest categoryCouponRequest) {

		categoryCouponService.registerCategoryCoupon(categoryCouponRequest);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new ResponseMessage(HttpStatus.CREATED.value(), "카테고리와 쿠폰 연결 성공"));
	}
}
