package shop.nuribooks.books.member.member.dto;

import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.member.dto.request.MemberRegisterRequest;

/**
 * @author Jprotection
 */
public class EntityMapper {

	/**
	 * MemberRegisterRequest를 Customer로 변환
	 */
	public static Customer toCustomerEntity(MemberRegisterRequest request) {
		return Customer.builder()
			.name(request.name())
			.password(request.password())
			.phoneNumber(request.phoneNumber())
			.email(request.email())
			.build();
	}
}

