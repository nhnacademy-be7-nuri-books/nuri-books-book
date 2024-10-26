package shop.nuribooks.books.member.member.dto;

import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.member.dto.request.MemberRegisterRequest;

public class EntityMapper {

	public static Customer toCustomerEntity(MemberRegisterRequest request) {
		return Customer.builder()
			.name(request.getName())
			.password(request.getPassword())
			.phoneNumber(request.getPhoneNumber())
			.email(request.getEmail())
			.build();
	}
}
