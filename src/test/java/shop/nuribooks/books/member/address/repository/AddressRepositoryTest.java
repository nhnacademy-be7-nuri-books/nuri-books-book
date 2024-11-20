package shop.nuribooks.books.member.address.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.common.config.QuerydslConfiguration;
import shop.nuribooks.books.member.address.entity.Address;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.customer.repository.CustomerRepository;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.grade.repository.GradeRepository;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@DataJpaTest
@Import(QuerydslConfiguration.class)
class AddressRepositoryTest {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private GradeRepository gradeRepository;

	@DisplayName("유저의 주소를 가져온다.")
	@Test
	void findAllByMember_UserId() {
		// given
		Grade grade = TestUtils.creategrade();
		gradeRepository.save(grade);

		Customer customer = TestUtils.createCustomer();
		customerRepository.save(customer);

		Member member = TestUtils.createMember(customer, grade);
		memberRepository.save(member);

		Address address = createAddress(member);
		addressRepository.save(address);
		// when
		List<Address> allByMemberId = addressRepository.findAllByMemberId(member.getId());

		// then
		assertThat(allByMemberId).hasSize(1);
	}

	private Address createAddress(Member member) {
		Address address = Address.builder()
			.name("test")
			.address("장말로")
			.detailAddress("103호")
			.isDefault(true)
			.build();
		address.setMember(member);
		return address;
	}

}
