package shop.nuribooks.books.member.member.dto;

import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.member.dto.response.MemberCheckResponse;
import shop.nuribooks.books.member.member.dto.response.MemberRegisterResponse;
import shop.nuribooks.books.member.member.dto.response.MemberUpdateResponse;
import shop.nuribooks.books.member.member.entity.Member;

public class DtoMapper {

	public static MemberRegisterResponse toRegisterDto(Customer customer, Member member) {
		return MemberRegisterResponse.builder()
			.name(customer.getName())
			.userId(member.getUserId())
			.phoneNumber(customer.getPhoneNumber())
			.email(customer.getEmail())
			.birthday(member.getBirthday())
			.build();
	}

	public static MemberUpdateResponse toUpdateDto(Customer customer) {
		return MemberUpdateResponse.builder()
			.name(customer.getName())
			.phoneNumber(customer.getPhoneNumber())
			.build();
	}

	public static MemberCheckResponse toNullDto() {
		return MemberCheckResponse.builder().build();
	}

	public static MemberCheckResponse toCheckDto(Customer customer, Member member) {
		return MemberCheckResponse.builder()
			.name(customer.getName())
			.password(customer.getPassword())
			.authority("ROLE_" + member.getAuthority().name())
			.build();
	}
}
