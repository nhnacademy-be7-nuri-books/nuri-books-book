package shop.nuribooks.books.member.customer.dto;

import java.math.BigDecimal;
import java.util.List;

import shop.nuribooks.books.member.address.dto.response.AddressResponse;

public record CustomerDto(
	Long userId,
	String name,
	String phoneNumber,
	String email,
	BigDecimal point,
	List<AddressResponse> addressList
) {
}
