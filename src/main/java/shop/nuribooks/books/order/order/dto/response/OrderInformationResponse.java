package shop.nuribooks.books.order.order.dto.response;

import java.math.BigDecimal;
import java.util.List;

import shop.nuribooks.books.book.book.dto.BookOrderResponse;
import shop.nuribooks.books.member.address.dto.response.AddressResponse;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.order.shipping.entity.ShippingPolicy;

public record OrderInformationResponse(

	// 사용자 관련 정보
	Long userId,
	String name,
	String phoneNumber,
	String email,
	BigDecimal point,
	List<AddressResponse> addressList,

	// 상품 리스트
	List<BookOrderResponse> bookOrderResponse,

	// 배송 관련 정보
	Long shippingPolicyId,
	int shippingFee,

	// 특이 사항 시 발생되는 메시지
	String message

	// todo: 쿠폰
) {

	/**
	 * 회원 주문 폼 시 가져올 항목
	 *
	 * @param customer
	 * @param addressList
	 * @param bookOrderResponses
	 * @param shippingPolicy
	 * @return
	 */
	public static OrderInformationResponse of(
		Customer customer,
		List<AddressResponse> addressList,
		List<BookOrderResponse> bookOrderResponses,
		ShippingPolicy shippingPolicy,
		BigDecimal point) {
		return new OrderInformationResponse(
			customer.getId(),
			customer.getName(),
			customer.getPhoneNumber(),
			customer.getEmail(),
			point,
			addressList,
			bookOrderResponses,
			shippingPolicy.getId(),
			shippingPolicy.getShippingFee(),
			null
		);
	}

	/**
	 * 비회원 주문 시 불러올 항목
	 *
	 * @param bookOrderResponses 상품 목록
	 * @param shippingPolicy 적용할 배송비 정책
	 * @return
	 */
	public static OrderInformationResponse of(
		List<BookOrderResponse> bookOrderResponses,
		ShippingPolicy shippingPolicy) {
		return new OrderInformationResponse(
			null,
			null,
			null,
			null,
			BigDecimal.ZERO,
			null,
			bookOrderResponses,
			shippingPolicy.getId(),
			shippingPolicy.getShippingFee(),
			null
		);
	}
}
