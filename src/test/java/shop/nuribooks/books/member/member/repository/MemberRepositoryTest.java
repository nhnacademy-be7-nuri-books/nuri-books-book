package shop.nuribooks.books.member.member.repository;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import shop.nuribooks.books.common.config.QuerydslConfiguration;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.customer.repository.CustomerRepository;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.grade.repository.GradeRepository;
import shop.nuribooks.books.member.member.dto.request.MemberSearchRequest;
import shop.nuribooks.books.member.member.dto.response.MemberSearchResponse;
import shop.nuribooks.books.member.member.entity.AuthorityType;
import shop.nuribooks.books.member.member.entity.GenderType;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.entity.StatusType;

@DataJpaTest
@Import(QuerydslConfiguration.class)
class MemberRepositoryTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private GradeRepository gradeRepository;

	@DisplayName("입력된 username으로 회원 등록 여부 확인")
	@Test
	void existsByUsername() {
		//given
		Member savedMember = getSavedMember();

		//when
		boolean exists = memberRepository.existsByUsername(savedMember.getUsername());

		//then
		assertThat(exists).isTrue();
	}

	@DisplayName("입력된 username으로 회원 조회")
	@Test
	void findByUsername() {
		//given
		Member savedMember = getSavedMember();

		//when
		Optional<Member> foundMember = memberRepository.findByUsername(savedMember.getUsername());

		//then
		assertThat(foundMember).isPresent(); // 회원이 존재함을 확인
		assertThat(foundMember.get().getId()).isEqualTo(savedMember.getId());
		assertThat(foundMember.get().getUsername()).isEqualTo(savedMember.getUsername());
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

	@DisplayName("마지막 로그인 후 90일이 지난 회원들을 조회")
	@Test
	void findAllByLatestLoginAtBefore() {
		//given
		Member savedMember = getSavedMember();
		savedMember.changeToInactive();
		LocalDateTime thresholdDate = LocalDateTime.now().plusYears(90);

		//when
		List<Member> inactiveMembers = memberRepository.findAllByLatestLoginAtBefore(thresholdDate);

		//then
		assertThat(inactiveMembers).hasSize(1);
		assertThat(inactiveMembers).extracting(Member::getUsername)
			.containsExactly(savedMember.getUsername());
	}

	@DisplayName("탈퇴 후 1년이 지난 회원들을 조회")
	@Test
	void findAllByWithdrawnAtBefore() {
		//given
		Member savedMember = getSavedMember();
		savedMember.changeToWithdrawn();
		LocalDateTime thresholdDate = LocalDateTime.now().plusYears(1);

		//when
		List<Member> withdrawnMembers = memberRepository.findAllByWithdrawnAtBefore(thresholdDate);

		//then
		assertThat(withdrawnMembers).hasSize(1);
		assertThat(withdrawnMembers).extracting(Member::getUsername)
			.containsExactly(savedMember.getUsername());
	}

	@DisplayName("다양한 검색 조건으로 회원 검색")
	@Test
	void searchMembersWithPaging() {
		//given
		Member savedMember = getSavedMember();

		MemberSearchRequest searchRequest = MemberSearchRequest.builder()
			.name("nuri")
			.gender(GenderType.MALE)
			.build();

		PageRequest pageRequest = PageRequest.of(0, 50);

		//when
		Page<MemberSearchResponse> result =
			memberRepository.searchMembersWithPaging(searchRequest, pageRequest);

		//then
		assertThat(result).hasSize(1);
		assertThat(result.getContent().getFirst().gender()).isEqualTo(savedMember.getGender());
	}

	@DisplayName("다양한 검색 조건으로 회원 검색")
	@Test
	void searchMembersWithPaging_nullRequest() {
		//given
		Member savedMember = getSavedMember();

		MemberSearchRequest searchRequest = MemberSearchRequest.builder()
			.build();

		PageRequest pageRequest = PageRequest.of(0, 50);

		//when
		Page<MemberSearchResponse> result =
			memberRepository.searchMembersWithPaging(searchRequest, pageRequest);

		//then
		assertThat(result).hasSize(1);
		assertThat(result.getContent().getFirst().gender()).isEqualTo(savedMember.getGender());
	}

	@DisplayName("다양한 검색 조건으로 회원 검색")
	@Test
	void searchMembersWithPaging_manyConditions() {
		//given
		Member savedMember = getSavedMember();

		MemberSearchRequest searchRequest = MemberSearchRequest.builder()
			.name("nuri")
			.gender(GenderType.MALE)
			.phoneNumber("01029837822")
			.email("email@contains.com")
			.birthdayGoe(LocalDate.now())
			.birthdayLoe(LocalDate.now())
			.username("username")
			.pointGoe(BigDecimal.TEN)
			.pointLoe(BigDecimal.ONE)
			.totalPaymentAmountGoe(BigDecimal.ZERO)
			.totalPaymentAmountLoe(BigDecimal.TEN)
			.authority(AuthorityType.ADMIN)
			.gradeName("grade")
			.status(StatusType.ACTIVE)
			.createdAtGoe(LocalDateTime.now())
			.createdAtLoe(LocalDateTime.now())
			.latestLoginAtGoe(LocalDateTime.now())
			.latestLoginAtLoe(LocalDateTime.now())
			.build();

		PageRequest pageRequest = PageRequest.of(0, 50);

		//when
		Page<MemberSearchResponse> result =
			memberRepository.searchMembersWithPaging(searchRequest, pageRequest);

		//then
		assertThat(result).isEmpty();
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
			.phoneNumber("01082828282")
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
			.username("nuribooks95")
			.birthday(LocalDate.of(1988, 8, 12))
			.createdAt(LocalDateTime.now())
			.point(BigDecimal.ZERO)
			.totalPaymentAmount(BigDecimal.ZERO)
			.latestLoginAt(LocalDateTime.now())
			.build();
	}
}
