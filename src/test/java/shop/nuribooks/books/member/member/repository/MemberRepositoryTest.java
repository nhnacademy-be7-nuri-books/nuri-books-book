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
import shop.nuribooks.books.member.member.entity.AuthorityType;
import shop.nuribooks.books.member.member.entity.GenderType;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.entity.StatusType;

@DataJpaTest
class MemberRepositoryTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private GradeRepository gradeRepository;

	@DisplayName("입력된 userId로 회원 등록 여부 확인")
	@Test
	void existsByUserId() {
		//given
		Member savedMember = getSavedMember();

		//when
		boolean exists = memberRepository.existsByUserId(savedMember.getUserId());

		//then
		assertThat(exists).isTrue();
	}

	@DisplayName("입력된 userId로 회원 조회")
	@Test
	void findByUserId() {
		//given
		Member savedMember = getSavedMember();

		//when
		Optional<Member> foundMember = memberRepository.findByUserId(savedMember.getUserId());

		//then
		assertThat(foundMember).isPresent(); // 회원이 존재함을 확인
		assertThat(foundMember.get().getId()).isEqualTo(savedMember.getId()); // ID가 같은지 확인
		assertThat(foundMember.get().getUserId()).isEqualTo(savedMember.getUserId()); // UserId가 같은지 확인
	}

	@DisplayName("등급의 id로 회원 등록 여부 확인")
	@Test
	void existsByGradeId() {
	    //given
		Member savedMember = getSavedMember();

		//when
		boolean exists = memberRepository.existsByGradeId(savedMember.getGrade().getId());

		//then
		assertThat(exists).isTrue();
	}

	/**
	 * 테스트를 위해 repository에 grade, customer, member 저장 후 member 반환
	 */
	private Member getSavedMember() {
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
			.authority(AuthorityType.MEMBER)
			.grade(savedGrade)
			.status(StatusType.ACTIVE)
			.gender(GenderType.MALE)
			.userId("nuribooks95")
			.birthday(LocalDate.of(1988, 8, 12))
			.createdAt(LocalDateTime.now())
			.point(BigDecimal.ZERO)
			.totalPaymentAmount(BigDecimal.ZERO)
			.build();
	}
}