package shop.nuribooks.books.order.orderDetail.dto;

import java.math.BigDecimal;

import shop.nuribooks.books.order.stub.coupon.BookAppliedCouponRequestStub;

public record OrderDetailRequest(
	Long bookId, // 책 정보
	int count, // 주문 수량
	BigDecimal unitPrice, // 단가
	boolean isWrapped, // 포장 여부
	BookAppliedCouponRequestStub bookCouponId // 도서 전용 쿠폰 (stub 객체 사용 이후 변경 예정)
) {

}
