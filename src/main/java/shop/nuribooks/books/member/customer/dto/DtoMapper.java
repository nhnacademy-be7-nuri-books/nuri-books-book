package shop.nuribooks.books.member.customer.dto;

import shop.nuribooks.books.member.customer.dto.response.CustomerRegisterResponse;
import shop.nuribooks.books.member.customer.dto.response.CustomerUpdateResponse;
import shop.nuribooks.books.member.customer.entity.Customer;

/**
 * @author Jprotection
 */
public class DtoMapper {

	/**
	 * Customer를 CustomerRegisterResponse로 변환
	 */
	public static CustomerRegisterResponse toRegisterDto(Customer customer) {
		return CustomerRegisterResponse.builder()
			.name(customer.getName())
			.phoneNumber(customer.getPhoneNumber())
			.email(customer.getEmail())
			.build();
	}

	/**
	 * Customer를 CustomerUpdateResponse로 변환
	 */
	public static CustomerUpdateResponse toUpdateDto(Customer customer) {
		return CustomerUpdateResponse.builder()
			.name(customer.getName())
			.phoneNumber(customer.getPhoneNumber())
			.build();
	}
}
