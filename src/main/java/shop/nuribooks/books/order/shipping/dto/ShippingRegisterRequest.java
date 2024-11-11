package shop.nuribooks.books.order.shipping.dto;

public record ShippingRegisterRequest(
	ShippingPolicyRequest shippingPolicy,
	String recipientName,
	String recipientAddress,
	String recipientAddressDetail,
	String recipientZipcode,
	String recipientPhoneNumber,
	String senderName,
	String senderPhoneNumber
) {
}
