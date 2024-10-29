package shop.nuribooks.books.member.member.dto;

import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.member.dto.response.MemberCheckResponse;
import shop.nuribooks.books.member.member.dto.response.MemberRegisterResponse;
import shop.nuribooks.books.member.member.dto.response.MemberUpdateResponse;
import shop.nuribooks.books.member.member.entity.Member;

/**
 * @author Jprotection
 */
public class DtoMapper {

	/**
	 * Customer와 Member를 MemberRegisterResponse로 변환
	 */
	public static MemberRegisterResponse toRegisterDto(Customer customer, Member member) {
		return MemberRegisterResponse.builder()
			.name(customer.getName())
			.gender(member.getGender())
			.userId(member.getUserId())
			.phoneNumber(customer.getPhoneNumber())
			.email(customer.getEmail())
			.birthday(member.getBirthday())
			.build();
	}

	/**
	 * Customer를 MemberUpdateResponse로 변환
	 */
	public static MemberUpdateResponse toUpdateDto(Customer customer) {
		return MemberUpdateResponse.builder()
			.name(customer.getName())
			.phoneNumber(customer.getPhoneNumber())
			.build();
	}

	/**
	 * 모든 필드가 null인 MemberCheckResponse를 반환
	 */
	public static MemberCheckResponse toNullDto() {
		return MemberCheckResponse.builder().build();
	}

	/**
	 * Customer와 Member를 MemberCheckResponse로 변환
	 */
	public static MemberCheckResponse toCheckDto(Customer customer, Member member) {
		return MemberCheckResponse.builder()
			.name(customer.getName())
			.password(customer.getPassword())
			.authority("ROLE_" + member.getAuthority().name())
			.build();
	}
}
