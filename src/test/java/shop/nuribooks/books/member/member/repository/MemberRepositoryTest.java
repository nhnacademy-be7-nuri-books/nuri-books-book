package shop.nuribooks.books.member.member.repository;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.customer.repository.CustomerRepository;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.grade.repository.GradeRepository;
import shop.nuribooks.books.member.member.entity.AuthorityEnum;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.entity.StatusEnum;

@DataJpaTest
class MemberRepositoryTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private GradeRepository gradeRepository;

	@DisplayName("입력된 id로 회원 존재 여부 확인")
	@Test
	void existsByUserId() {
		//given
		Member savedMember = getReadyMember();

		//when
		boolean exists = memberRepository.existsByUserId(savedMember.getUserId());

		//then
		assertThat(exists).isTrue();
	}

	@DisplayName("입력된 id로 회원 조회")
	@Test
	void findByUserId() {
		//given
		Member savedMember = getReadyMember();

		//when
		Optional<Member> foundMember = memberRepository.findByUserId(savedMember.getUserId());

		//then
		assertThat(foundMember).isPresent(); // 회원이 존재함을 확인
		assertThat(foundMember.get().getId()).isEqualTo(savedMember.getId()); // ID가 같은지 확인
		assertThat(foundMember.get().getUserId()).isEqualTo(savedMember.getUserId()); // UserId가 같은지 확인
	}

	/**
	 * 테스트를 위한 초기화 작업들 후 repository에 저장된 회원 반환
	 */
	private Member getReadyMember() {
		Grade savedGrade = gradeRepository.save(createGrade());
		Customer savedCustomer = customerRepository.save(createCustomer());
		Member readyMember = createMember(savedCustomer, savedGrade);
		return memberRepository.save(readyMember);
	}

	/**
	 * 테스트를 위한 등급 생성
	 */
	private Grade createGrade() {
		return Grade.builder()
			.name("STANDARD")
			.pointRate(3)
			.requirement(BigDecimal.valueOf(100_000))
			.build();
	}

	/**
	 * 테스트를 위한 비회원 생성
	 */
	private Customer createCustomer() {
		return Customer.builder()
			.name("nuri")
			.password("abc123")
			.phoneNumber("042-8282-8282")
			.email("nhnacademy@nuriBooks.com")
			.build();
	}

	/**
	 * 테스트를 위한 멤버 생성
	 */
	private Member createMember(Customer savedCustomer, Grade savedGrade) {
		return Member.builder()
			.customer(savedCustomer)
			.authority(AuthorityEnum.MEMBER)
			.grade(savedGrade)
			.status(StatusEnum.ACTIVE)
			.userId("nuribooks95")
			.birthday(LocalDate.of(1988, 8, 12))
			.createdAt(LocalDateTime.now())
			.point(BigDecimal.ZERO)
			.totalPaymentAmount(BigDecimal.ZERO)
			.build();
	}
}