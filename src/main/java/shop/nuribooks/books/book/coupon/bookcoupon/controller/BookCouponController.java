package shop.nuribooks.books.book.coupon.bookcoupon.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.coupon.bookcoupon.dto.BookCouponRequest;
import shop.nuribooks.books.book.coupon.bookcoupon.dto.BookCouponResponse;
import shop.nuribooks.books.book.coupon.bookcoupon.service.BookCouponService;
import shop.nuribooks.books.common.annotation.HasRole;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.member.member.entity.AuthorityType;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons/book-coupons")
public class BookCouponController {
	private final BookCouponService bookCouponService;

	@HasRole(role = AuthorityType.ADMIN)
	@PostMapping
	public ResponseEntity<ResponseMessage> registerBookCoupon(
		@Valid @RequestBody BookCouponRequest bookCouponRequest) {

		bookCouponService.registerBookCoupon(bookCouponRequest);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new ResponseMessage(HttpStatus.CREATED.value(), "도서와 쿠폰 연결 성공"));
	}

	@GetMapping("/{book-id}")
	public ResponseEntity<BookCouponResponse> getBookCoupon(@Valid @PathVariable(name = "book-id") Long bookId) {
		BookCouponResponse bookCouponResponse = bookCouponService.getBookCoupon(bookId);
		return ResponseEntity.status(HttpStatus.OK)
			.body(bookCouponResponse);

	}
}
