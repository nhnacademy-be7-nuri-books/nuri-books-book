package shop.nuribooks.books.repository.member;

import static java.math.BigDecimal.*;
import static org.assertj.core.api.Assertions.*;
import static shop.nuribooks.books.entity.member.AuthorityEnum.*;
import static shop.nuribooks.books.entity.member.GradeEnum.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import shop.nuribooks.books.entity.member.Customer;
import shop.nuribooks.books.entity.member.Member;

@DataJpaTest
class MemberRepositoryTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@DisplayName("입력된 id로 회원 존재 여부 확인")
	@Test
	public void existsByUserId() {
		//given
		Customer customer = customer();
		Customer savedCustomer = customerRepository.save(customer);
		Member member = member(savedCustomer);
		Member savedMember = memberRepository.save(member);

		//when
		boolean exists = memberRepository.existsByUserId(savedMember.getUserId());

		//then
		assertThat(exists).isTrue();
	}

	private Member member(Customer savedCustomer) {
		return Member.builder()
			.customer(savedCustomer)
			.authority(MEMBER)
			.grade(STANDARD)
			.userId("nuri95")
			.birthday(LocalDate.of(1988, 8, 12))
			.createdAt(LocalDateTime.now())
			.point(ZERO)
			.totalPaymentAmount(ZERO)
			.latestLoginAt(null)
			.withdrawnAt(null)
			.build();
	}

	private Customer customer() {
		return Customer.builder()
			.id(null)
			.name("nuri")
			.password("abc123")
			.phoneNumber("042-8282-8282")
			.email("nhnacademy@nuriBooks.com")
			.build();
	}
}