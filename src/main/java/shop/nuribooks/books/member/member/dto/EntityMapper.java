package shop.nuribooks.books.member.member.dto;

import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.member.dto.request.MemberRegisterRequest;

public class EntityMapper {

	public static Customer toCustomerEntity(MemberRegisterRequest request) {
		return Customer.builder()
			.name(request.name())
			.password(request.password())
			.phoneNumber(request.phoneNumber())
			.email(request.email())
			.build();
	}
}
