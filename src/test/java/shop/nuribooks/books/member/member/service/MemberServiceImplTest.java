package shop.nuribooks.books.member.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.grade.repository.GradeRepository;
import shop.nuribooks.books.member.member.dto.EntityMapper;
import shop.nuribooks.books.member.member.dto.request.MemberRegisterRequest;
import shop.nuribooks.books.member.member.dto.request.MemberUpdateRequest;
import shop.nuribooks.books.member.member.dto.request.MemberWithdrawRequest;
import shop.nuribooks.books.member.member.dto.response.MemberCheckResponse;
import shop.nuribooks.books.member.member.dto.response.MemberRegisterResponse;
import shop.nuribooks.books.member.member.dto.response.MemberUpdateResponse;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.member.entity.AuthorityEnum;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.exception.member.CustomerNotFoundException;
import shop.nuribooks.books.exception.member.EmailAlreadyExistsException;
import shop.nuribooks.books.exception.member.InvalidPasswordException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.exception.member.UserIdAlreadyExistsException;
import shop.nuribooks.books.exception.member.UserIdNotFoundException;
import shop.nuribooks.books.member.customer.repository.CustomerRepository;
import shop.nuribooks.books.member.member.entity.StatusEnum;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

	@InjectMocks
	private MemberServiceImpl memberServiceImpl;

	@Mock
	private GradeRepository gradeRepository;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private MemberRepository memberRepository;

	@DisplayName("회원 등록 성공")
	@Test
	void registerMember() {
		// given
		MemberRegisterRequest request = getMemberCreateRequest();
		Grade standard = getGrade();
		Customer savedCustomer = EntityMapper.toCustomerEntity(request);
		Member savedMember = getMember(savedCustomer);

		when(customerRepository.existsByEmail(request.email())).thenReturn(false);
		when(memberRepository.existsByUserId(request.userId())).thenReturn(false);
		when(customerRepository.save(any(Customer.class)))
			.thenReturn(savedCustomer);
		when(gradeRepository.findByName("STANDARD")).thenReturn(Optional.of(standard));
		when(memberRepository.save(any(Member.class)))
			.thenReturn(savedMember);

		// when
		MemberRegisterResponse response = memberServiceImpl.registerMember(request);

		// then
		assertThat(response.userId()).isEqualTo(request.userId());
		assertThat(response.phoneNumber()).isEqualTo(request.phoneNumber());
		assertThat(response.email()).isEqualTo(request.email());

		// verify
		verify(customerRepository, times(1)).save(any(Customer.class));
		verify(memberRepository, times(1)).save(any(Member.class));
	}

	@DisplayName("회원 등록 실패 - 중복된 이메일")
	@Test
	public void registerMember_EmailAlreadyExists() {
	    //given
		MemberRegisterRequest request = getMemberCreateRequest();
		when(customerRepository.existsByEmail(request.email())).thenReturn(true);

	    //when / then
		EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class,
			() -> memberServiceImpl.registerMember(request));
		assertThat(exception.getMessage()).isEqualTo("이미 존재하는 이메일입니다.");

	}

	@DisplayName("회원 등록 실패 - 중복된 아이디")
	@Test
	void registerMember_UserIdAlreadyExists() {
	    //given
		MemberRegisterRequest request = getMemberCreateRequest();

		when(customerRepository.existsByEmail(request.email())).thenReturn(false);
		when(memberRepository.existsByUserId(request.userId())).thenReturn(true);

		//when / then
		UserIdAlreadyExistsException exception = assertThrows(UserIdAlreadyExistsException.class,
			() -> memberServiceImpl.registerMember(request));
		assertThat(exception.getMessage()).isEqualTo("이미 존재하는 아이디입니다.");
	}

	@DisplayName("회원 탈퇴 성공")
	@Test
	void withdrawMember() {
	    //given
		MemberWithdrawRequest request = getMemberWithdrawRequest();

		Customer existingCustomer = getCustomer();
		Member existingMember = spy(getMember(existingCustomer));

		when(memberRepository.findByUserId(request.userId())).thenReturn(Optional.of(existingMember));
		when(customerRepository.existsByIdAndPassword(existingMember.getId(), request.password())).thenReturn(true);

		//when
		memberServiceImpl.withdrawMember(request);

	    //then
		verify(existingMember, times(1)).changeToWithdrawn(); // 메서드 호출 확인
		assertThat(existingMember.getStatus()).isEqualTo(StatusEnum.WITHDRAWN); // 상태가 WITHDRAWN로 변경되었는지 확인
		assertThat(existingMember.getWithdrawnAt()).isNotNull(); // withdrawnAt이 현재 시간으로 설정되었는지 확인
	}

	@DisplayName("회원 탈퇴 실패 - 존재하지 않는 아이디")
	@Test
	void withdrawMember_UserIdNotFound() {
	    //given
		MemberWithdrawRequest request = getMemberWithdrawRequest();

		when(memberRepository.findByUserId(request.userId())).thenReturn(Optional.empty());

	    //when / then
		UserIdNotFoundException exception = assertThrows(UserIdNotFoundException.class,
			() -> memberServiceImpl.withdrawMember(request));
		assertThat(exception.getMessage()).isEqualTo("존재하지 않는 아이디입니다.");
	}

	@DisplayName("회원 탈퇴 실패 - 비밀번호 불일치")
	@Test
	void withdrawMember_InvalidPassword() {
		//given
		MemberWithdrawRequest request = getMemberWithdrawRequest();

		Customer existingCustomer = getCustomer();
		Member existingMember = getMember(existingCustomer);

		when(memberRepository.findByUserId(request.userId())).thenReturn(Optional.of(existingMember));
		when(customerRepository.existsByIdAndPassword(existingMember.getId(), request.password()))
			.thenReturn(false);

		//when / then
		InvalidPasswordException exception = assertThrows(InvalidPasswordException.class,
			() -> memberServiceImpl.withdrawMember(request));
		assertThat(exception.getMessage()).isEqualTo("비밀번호가 일치하지 않습니다.");
	}

	@DisplayName("회원 정보 수정 성공")
	@Test
	void updateMember() {
	    //given
		MemberUpdateRequest request = getMemberUpdateRequest();
		Customer existingCustomer = spy(getCustomer());
		Member existingMember = getMember(existingCustomer);

		when(memberRepository.findByUserId(existingMember.getUserId())).thenReturn(Optional.of(existingMember));
		when(customerRepository.findById(existingMember.getId())).thenReturn(Optional.of(existingCustomer));

	    //when
		MemberUpdateResponse response = memberServiceImpl.updateMember(existingMember.getUserId(), request);

	    //then
		verify(existingCustomer, times(1))
			.changeCustomerInformation(request.name(), request.password(), request.phoneNumber());
		assertThat(response.name()).isEqualTo(existingCustomer.getName());
		assertThat(response.phoneNumber()).isEqualTo(existingCustomer.getPhoneNumber());
	}

	@DisplayName("회원 정보 수정 실패 - 존재하지 않는 회원")
	@Test
	void updateMember_MemberNotFound() {
	    //given
		MemberUpdateRequest request = getMemberUpdateRequest();
		String requestUserId = "nhnacademy";

		when(memberRepository.findByUserId(requestUserId)).thenReturn(Optional.empty());

	    //when / then
		MemberNotFoundException exception = assertThrows(MemberNotFoundException.class,
			() -> memberServiceImpl.updateMember(requestUserId, request));
		assertThat(exception.getMessage()).isEqualTo("존재하지 않는 회원입니다.");
	}

	@DisplayName("회원 정보 수정 실패 - 존재하지 않는 고객")
	@Test
	void updateMember_CustomerNotFound() {
		//given
		MemberUpdateRequest request = getMemberUpdateRequest();
		Customer existingCustomer = getCustomer();
		Member existingMember = getMember(existingCustomer);

		String requestUserId = "nhnacademy";

		when(memberRepository.findByUserId(requestUserId)).thenReturn(Optional.of(existingMember));
		when(customerRepository.findById(existingMember.getId())).thenReturn(Optional.empty());

		//when / then
		CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
			() -> memberServiceImpl.updateMember(requestUserId, request));
		assertThat(exception.getMessage()).isEqualTo("존재하지 않는 고객입니다.");
	}

	@DisplayName("회원 이름, 비밀번호, 권한 조회 성공")
	@Test
	void checkMember() {
	    //given
		Customer existingCustomer = getCustomer();
		Member existingMember = getMember(existingCustomer);

		when(memberRepository.findByUserId(existingMember.getUserId())).thenReturn(Optional.of(existingMember));
		when(customerRepository.findById(existingMember.getId())).thenReturn(Optional.of(existingCustomer));

	    //when
		MemberCheckResponse response = memberServiceImpl.checkMember(existingMember.getUserId());

		//then
		assertThat(response.name()).isEqualTo(existingCustomer.getName());
		assertThat(response.password()).isEqualTo(existingCustomer.getPassword());
		assertThat(response.authority()).isEqualTo("ROLE_" + existingMember.getAuthority().name());
	}

	@DisplayName("회원 이름, 비밀번호, 권한 조회 실패 - 회원이 존재하지 않을 때")
	@Test
	void checkMember_UserIdNotFound() {
		//given
		String requestUserid = "nhnacademy";

		when(memberRepository.findByUserId(requestUserid)).thenReturn(Optional.empty());

		//when
		MemberCheckResponse response = memberServiceImpl.checkMember(requestUserid);

		//then
		assertNotNull(response);
		assertNull(response.name());
		assertNull(response.password());
		assertNull(response.authority());
	}

	@DisplayName("회원 이름, 비밀번호, 권한 조회 실패 - 고객이 존재하지 않을 때")
	@Test
	void checkMember_CustomerNotFound() {
		//given
		Customer existingCustomer = getCustomer();
		Member existingMember = getMember(existingCustomer);
		String requestUserid = "nhnacademy";

		when(memberRepository.findByUserId(requestUserid)).thenReturn(Optional.of(existingMember));
		when(customerRepository.findById(existingMember.getId())).thenReturn(Optional.empty());

		//when
		MemberCheckResponse response = memberServiceImpl.checkMember(requestUserid);

		//then
		assertNotNull(response);
		assertNull(response.name());
		assertNull(response.password());
		assertNull(response.authority());
	}

	/**
	 * 테스트를 위한 MemberRegisterRequest 생성
	 */
	private MemberRegisterRequest getMemberCreateRequest() {
		return MemberRegisterRequest.builder()
			.name("boho")
			.userId("nuribooks95")
			.password("abc123")
			.phoneNumber("042-8282-8282")
			.email("nhnacademy@nuriBooks.com")
			.birthday(LocalDate.of(1988, 8, 12))
			.build();
	}

	/**
	 * 테스트를 위한 MemberUpdateRequest 생성
	 */
	private MemberUpdateRequest getMemberUpdateRequest() {
		return MemberUpdateRequest.builder()
			.name("수정된 이름")
			.password("수정된 비밀번호")
			.phoneNumber("010-0000-000")
			.build();
	}

	/**
	 * 테스트를 위한 MemberWithdrawRequest 생성
	 */
	private MemberWithdrawRequest getMemberWithdrawRequest() {
		return MemberWithdrawRequest.builder()
			.userId("nuribooks95")
			.password("abc123")
			.build();
	}

	/**
	 * 테스트를 위한 등급 생성
	 */
	private Grade getGrade() {
		return Grade.builder()
			.id(1)
			.name("STANDARD")
			.pointRate(3)
			.requirement(BigDecimal.valueOf(100_000))
			.build();
	}

	/**
	 * 테스트를 위한 비회원 생성
	 */
	private Customer getCustomer() {
		return Customer.builder()
			.id(1L)
			.name("boho")
			.password("abc123")
			.phoneNumber("042-8282-8282")
			.email("nhnacademy@nuriBooks.com")
			.build();
	}

	/**
	 * 테스트를 위한 회원 생성
	 */
	private Member getMember(Customer savedCustomer) {
		return Member.builder()
			.id(1L)
			.customer(savedCustomer)
			.authority(AuthorityEnum.MEMBER)
			.grade(getGrade())
			.status(StatusEnum.ACTIVE)
			.userId("nuribooks95")
			.birthday(LocalDate.of(1988, 8, 12))
			.createdAt(LocalDateTime.now())
			.point(BigDecimal.ZERO)
			.totalPaymentAmount(BigDecimal.ZERO)
			.build();
	}

	/**
	 * 테스트를 위한 동적 비회원 목록 생성
	 */
	private List<Customer> getDynamicCustomers(Long... ids) {
		return Arrays.stream(ids)
			.map(id -> Customer.builder()
				.id(id)
				.name("boho" + id)
				.password("abc123")
				.phoneNumber("042-8282-8282")
				.email("nhnacademy" + id + "@nuriBooks.com")
				.build())
			.toList();
	}

	/**
	 * 테스트를 위한 동적 탈퇴 회원 목록 생성
	 */
	private List<Member> getDynamicWithdrawnMembers(Long... ids) {
		return IntStream.range(0, ids.length)
				.mapToObj(i -> Member.builder()
					.id(ids[i])
					.customer(getDynamicCustomers(ids).get(i))
					.authority(AuthorityEnum.MEMBER)
					.grade(getGrade())
					.status(StatusEnum.WITHDRAWN)
					.userId("nuribooks95")
					.birthday(LocalDate.of(1988, 8, 12))
					.createdAt(LocalDateTime.now())
					.point(BigDecimal.ZERO)
					.totalPaymentAmount(BigDecimal.ZERO)
					.withdrawnAt(LocalDateTime.of(2025 - ids[i].intValue(), 5, 14, 15, 30, 57))
					.build())
			.toList();
	}
}