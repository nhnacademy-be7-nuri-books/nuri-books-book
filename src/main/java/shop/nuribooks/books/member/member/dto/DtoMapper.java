package shop.nuribooks.books.member.member.dto;

import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.member.dto.response.MemberAuthInfoResponse;
import shop.nuribooks.books.member.member.dto.response.MemberDetailsResponse;
import shop.nuribooks.books.member.member.dto.response.MemberRegisterResponse;
import shop.nuribooks.books.member.member.entity.Member;

/**
 * @author Jprotection
 */
public class DtoMapper {

	// 기본 생성자를 private으로 선언하여 외부에서 객체를 생성할 수 없게 함
	private DtoMapper() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
	}

	/**
	 * Customer와 Member를 MemberRegisterResponse로 변환
	 */
	public static MemberRegisterResponse toRegisterDto(Customer customer, Member member) {
		return MemberRegisterResponse.builder()
			.name(customer.getName())
			.gender(member.getGender())
			.username(member.getUsername())
			.phoneNumber(customer.getPhoneNumber())
			.email(customer.getEmail())
			.birthday(member.getBirthday())
			.build();
	}

	/**
	 * 모든 필드가 null인 MemberAuthInfoResponse를 반환
	 */
	public static MemberAuthInfoResponse toNullAuthInfoDto() {
		return MemberAuthInfoResponse.builder().build();
	}

	/**
	 * Customer와 Member를 MemberAuthInfoResponse로 변환
	 */
	public static MemberAuthInfoResponse toAuthInfoDto(Customer customer, Member member) {
		return MemberAuthInfoResponse.builder()
			.customerId(customer.getId())
			.username(member.getUsername())
			.password(customer.getPassword())
			.role("ROLE_" + member.getAuthority().name())
			.status("STATUS_" + member.getStatus().name())
			.build();
	}

	/**
	 * Customer와 Member를 MemberDetailsResponse로 변환
	 */
	public static MemberDetailsResponse toDetailsDto(Customer customer, Member member) {
		return MemberDetailsResponse.builder()
			.username(member.getUsername())
			.name(customer.getName())
			.phoneNumber(customer.getPhoneNumber())
			.email(customer.getEmail())
			.point(member.getPoint())
			.totalPaymentAmount(member.getTotalPaymentAmount())
			.gradeName(member.getGrade().getName())
			.pointRate(member.getGrade().getPointRate())
			.createdAt(member.getCreatedAt())
			.build();
	}
}
