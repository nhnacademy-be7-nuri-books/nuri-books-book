package shop.nuribooks.books.member.member.dto;

import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.member.dto.request.MemberRegisterRequest;

/**
 * @author Jprotection
 */
public class EntityMapper {

	// 기본 생성자를 private으로 선언하여 외부에서 객체를 생성할 수 없게 함
	private EntityMapper() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
	}

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
