package shop.nuribooks.books.member.member.dto;

import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.member.dto.response.MemberAuthInfoResponse;
import shop.nuribooks.books.member.member.dto.response.MemberDetailsResponse;
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
	 * 모든 필드가 null인 MemberAuthInfoResponse를 반환
	 */
	public static MemberAuthInfoResponse toNullDto() {
		return MemberAuthInfoResponse.builder().build();
	}

	/**
	 * Customer와 Member를 MemberAuthInfoResponse로 변환
	 */
	public static MemberAuthInfoResponse toAuthInfoDto(Customer customer, Member member) {
		return MemberAuthInfoResponse.builder()
			.username(member.getUserId())
			.password(customer.getPassword())
			.role("ROLE_" + member.getAuthority().name())
			.build();
	}

	/**
	 * Customer와 Member를 MemberDetailsResponse로 변환
	 */
	public static MemberDetailsResponse toDetailsDto(Customer customer, Member member) {
		return MemberDetailsResponse.builder()
			.name(customer.getName())
			.gender(member.getGender())
			.phoneNumber(customer.getPhoneNumber())
			.email(customer.getEmail())
			.birthday(member.getBirthday())
			.userId(member.getUserId())
			.password(customer.getPassword())
			.point(member.getPoint())
			.totalPaymentAmount(member.getTotalPaymentAmount())
			.authority(member.getAuthority())
			.grade(member.getGrade())
			.status(member.getStatus())
			.createdAt(member.getCreatedAt())
			.latestLoginAt(member.getLatestLoginAt())
			.build();

	}
}
