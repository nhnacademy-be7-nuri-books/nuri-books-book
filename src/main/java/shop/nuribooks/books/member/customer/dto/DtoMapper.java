package shop.nuribooks.books.member.customer.dto;

import shop.nuribooks.books.member.customer.dto.response.CustomerRegisterResponse;
import shop.nuribooks.books.member.customer.dto.response.CustomerUpdateResponse;
import shop.nuribooks.books.member.customer.entity.Customer;

public class DtoMapper {

	public static CustomerRegisterResponse toRegisterDto(Customer customer) {
		return CustomerRegisterResponse.builder()
			.name(customer.getName())
			.phoneNumber(customer.getPhoneNumber())
			.email(customer.getEmail())
			.build();
	}

	public static CustomerUpdateResponse toUpdateDto(Customer customer) {
		return CustomerUpdateResponse.builder()
			.name(customer.getName())
			.phoneNumber(customer.getPhoneNumber())
			.build();
	}
}
