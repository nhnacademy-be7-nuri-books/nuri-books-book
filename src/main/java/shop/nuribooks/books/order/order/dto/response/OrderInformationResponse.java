package shop.nuribooks.books.order.order.dto.response;

import java.util.List;

import lombok.Builder;
import shop.nuribooks.books.book.book.dto.BookOrderResponse;
import shop.nuribooks.books.book.coupon.dto.MemberCouponOrderDto;
import shop.nuribooks.books.member.customer.dto.CustomerDto;
import shop.nuribooks.books.order.shipping.dto.ShippingPolicyResponse;
import shop.nuribooks.books.order.wrapping.dto.WrappingPaperResponse;

@Builder
public record OrderInformationResponse(

	// 사용자 관련 정보
	CustomerDto customer,

	// 상품 리스트
	List<BookOrderResponse> bookOrderResponse,

	// 배송비 관련 정보
	ShippingPolicyResponse shippingPolicyResponse,

	// 포장지 정보
	List<WrappingPaperResponse> paperResponse,

	// 쿠폰 정보
	List<MemberCouponOrderDto> allTypeCoupon,

	// 특이 사항 시 발생되는 메시지
	String message

) {
}
