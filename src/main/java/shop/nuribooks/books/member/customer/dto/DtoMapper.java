package shop.nuribooks.books.member.customer.dto;

import shop.nuribooks.books.member.customer.dto.response.CustomerAuthInfoResponse;
import shop.nuribooks.books.member.customer.dto.response.CustomerRegisterResponse;
import shop.nuribooks.books.member.customer.entity.Customer;

/**
 * @author Jprotection
 */
public class DtoMapper {

	// 기본 생성자를 private으로 선언하여 외부에서 객체를 생성할 수 없게 함
	private DtoMapper() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
	}

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

	public static CustomerAuthInfoResponse toAuthInfoDto(Customer customer) {
		return CustomerAuthInfoResponse.builder()
			.customerId(customer.getId())
			.password(customer.getPassword())
			.email(customer.getEmail())
			.build();
	}
}
