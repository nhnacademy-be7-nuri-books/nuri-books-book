package shop.nuribooks.books.member.customer.dto;

import shop.nuribooks.books.member.customer.dto.request.CustomerRegisterRequest;
import shop.nuribooks.books.member.customer.entity.Customer;

/**
 * @author Jprotection
 */
public class EntityMapper {

	/**
	 * CustomerRegisterRequest를 Customer로 변환
	 */
	public static Customer toCustomerEntity(CustomerRegisterRequest request) {
		return Customer.builder()
			.name(request.name())
			.password(request.password())
			.phoneNumber(request.phoneNumber())
			.email(request.email())
			.build();
	}
}

