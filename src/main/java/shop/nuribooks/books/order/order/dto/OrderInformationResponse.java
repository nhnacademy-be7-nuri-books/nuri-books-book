package shop.nuribooks.books.order.order.dto;

import java.util.List;

import shop.nuribooks.books.book.book.dto.BookOrderResponse;
import shop.nuribooks.books.member.address.dto.response.AddressResponse;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.order.shipping.entity.ShippingPolicy;

public record OrderInformationResponse(
	Long userId,
	String name,
	String phoneNumber,
	String email,
	List<AddressResponse> addressList,
	List<BookOrderResponse> bookOrderResponse,
	Long shippingPolicyId,
	int shippingFee,
	String message

	// todo: 쿠폰
	// todo: 잔여 포인트
) {
	public static OrderInformationResponse of(
		Customer customer,
		List<AddressResponse> addressList,
		List<BookOrderResponse> bookOrderResponses,
		ShippingPolicy shippingPolicy){
		return new OrderInformationResponse(
			customer.getId(),
			customer.getName(),
			customer.getPhoneNumber(),
			customer.getEmail(),
			addressList,
			bookOrderResponses,
			shippingPolicy.getId(),
			shippingPolicy.getShippingFee(),
			null
		);
	}

	public static OrderInformationResponse of(
		List<BookOrderResponse> bookOrderResponses,
		ShippingPolicy shippingPolicy){
		return new OrderInformationResponse(
			null,
			null,
			null,
			null,
			null,
			bookOrderResponses,
			shippingPolicy.getId(),
			shippingPolicy.getShippingFee(),
			null
		);
	}
}
