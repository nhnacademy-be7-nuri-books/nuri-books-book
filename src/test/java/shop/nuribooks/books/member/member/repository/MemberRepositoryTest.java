package shop.nuribooks.books.member.member.repository;

import static java.math.BigDecimal.*;
import static org.assertj.core.api.Assertions.*;
import static shop.nuribooks.books.member.authority.entity.AuthorityType.MEMBER;
import static shop.nuribooks.books.member.member.entity.GradeEnum.STANDARD;
import static shop.nuribooks.books.member.member.entity.StatusEnum.ACTIVE;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import shop.nuribooks.books.member.member.entity.Customer;
import shop.nuribooks.books.member.member.entity.Member;

@DataJpaTest
class MemberRepositoryTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@DisplayName("입력된 id로 회원 존재 여부 확인")
	@Test
	void existsByUserId() {
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

	@DisplayName("입력된 id로 회원 조회")
	@Test
	void findByUserId() {
	    //given
		Customer customer = customer();
		Customer savedCustomer = customerRepository.save(customer);
		Member member = member(savedCustomer);
		Member savedMember = memberRepository.save(member);

	    //when
		Optional<Member> foundMember = memberRepository.findByUserId(savedMember.getUserId());

	    //then
		assertThat(foundMember).isPresent(); // 회원이 존재함을 확인
		assertThat(foundMember.get().getId()).isEqualTo(savedMember.getId()); // ID가 같은지 확인
		assertThat(foundMember.get().getUserId()).isEqualTo(savedMember.getUserId()); // UserId가 같은지 확인
	}

	private Member member(Customer savedCustomer) {
		return Member.builder()
			.customer(savedCustomer)
			.authority(MEMBER)
			.grade(STANDARD)
			.status(ACTIVE)
			.userId("nuribooks95")
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