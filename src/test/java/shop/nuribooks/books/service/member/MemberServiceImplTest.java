package shop.nuribooks.books.service.member;

import static java.math.BigDecimal.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static shop.nuribooks.books.entity.member.AuthorityEnum.*;
import static shop.nuribooks.books.entity.member.GradeEnum.*;
import static shop.nuribooks.books.entity.member.StatusEnum.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.nuribooks.books.dto.member.request.MemberRegisterRequest;
import shop.nuribooks.books.dto.member.request.MemberUpdateRequest;
import shop.nuribooks.books.dto.member.request.MemberWithdrawRequest;
import shop.nuribooks.books.dto.member.response.MemberCheckResponse;
import shop.nuribooks.books.dto.member.response.MemberRegisterResponse;
import shop.nuribooks.books.dto.member.response.MemberUpdateResponse;
import shop.nuribooks.books.entity.member.Customer;
import shop.nuribooks.books.entity.member.Member;
import shop.nuribooks.books.repository.member.CustomerRepository;
import shop.nuribooks.books.repository.member.MemberRepository;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

	@InjectMocks
	private MemberServiceImpl memberServiceImpl;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private MemberRepository memberRepository;

	@DisplayName("회원 등록 성공")
	@Test
	void registerMember() {
		// given
		MemberRegisterRequest request = memberCreateRequest();

		Customer savedCustomer = doReturn(Customer.builder()
			.name(request.getName())
			.password(request.getPassword())
			.phoneNumber(request.getPhoneNumber())
			.email(request.getEmail())
			.build())

			.when(customerRepository)
			.save(any(Customer.class));

		doReturn(member(savedCustomer))
			.when(memberRepository)
			.save(any(Member.class));

		// when
		MemberRegisterResponse response = memberServiceImpl.registerMember(request);

		// then
		assertThat(response.getUserId()).isEqualTo(request.getUserId());
		assertThat(response.getPhoneNumber()).isEqualTo(request.getPhoneNumber());
		assertThat(response.getEmail()).isEqualTo(request.getEmail());

		// verify
		verify(customerRepository, times(1)).save(any(Customer.class));
		verify(memberRepository, times(1)).save(any(Member.class));
	}

	@DisplayName("회원 탈퇴 성공")
	@Test
	void withdrawMember() {
	    //given
		MemberWithdrawRequest request = memberWithdrawRequest();

		Customer existingCustomer = customer();
		Member existingMember = spy(member(existingCustomer));

		when(memberRepository.existsByUserId(request.getUserId())).thenReturn(true);
		when(memberRepository.findByUserId(request.getUserId())).thenReturn(Optional.of(existingMember));
		when(customerRepository.existsByIdAndPassword(existingMember.getId(), request.getPassword())).thenReturn(true);

		//when
		memberServiceImpl.withdrawMember(request);

	    //then
		verify(existingMember, times(1)).changeToWithdrawn(); // 메서드 호출 확인
		assertThat(existingMember.getStatus()).isEqualTo(INACTIVE); // 상태가 INACTIVE로 변경되었는지 확인
		assertThat(existingMember.getWithdrawnAt()).isNotNull(); // withdrawnAt이 현재 시간으로 설정되었는지 확인
	}

	@DisplayName("회원 정보 수정 성공")
	@Test
	public void updateMember() {
	    //given
		MemberUpdateRequest request = memberUpdateRequest();
		Customer existingCustomer = spy(customer());
		Member existingMember = member(existingCustomer);

		when(memberRepository.findByUserId(existingMember.getUserId())).thenReturn(Optional.of(existingMember));
		when(customerRepository.findById(existingMember.getId())).thenReturn(Optional.of(existingCustomer));

	    //when
		MemberUpdateResponse response = memberServiceImpl.updateMember(existingMember.getUserId(), request);

	    //then
		verify(existingCustomer, times(1))
			.changeCustomerInformation(request.getName(), request.getPassword(), request.getPhoneNumber());
		assertThat(response.getName()).isEqualTo(existingCustomer.getName());
		assertThat(response.getPhoneNumber()).isEqualTo(existingCustomer.getPhoneNumber());
	}

	@DisplayName("회원 이름, 비밀번호, 권한 조회 성공")
	@Test
	public void checkMember() {
	    //given
		Customer existingCustomer = customer();
		Member existingMember = member(existingCustomer);

		when(memberRepository.findByUserId(existingMember.getUserId())).thenReturn(Optional.of(existingMember));
		when(customerRepository.findById(existingMember.getId())).thenReturn(Optional.of(existingCustomer));

	    //when
		MemberCheckResponse response = memberServiceImpl.checkMember(existingMember.getUserId());

		//then
		assertThat(response.getName()).isEqualTo(existingCustomer.getName());
		assertThat(response.getPassword()).isEqualTo(existingCustomer.getPassword());
		assertThat(response.getAuthority()).isEqualTo("ROLE_" + existingMember.getAuthority());
	}

	private MemberUpdateRequest memberUpdateRequest() {
		return MemberUpdateRequest.builder()
			.name("수정된 이름")
			.password("수정된 비밀번호")
			.phoneNumber("010-0000-000")
			.build();
	}

	private MemberWithdrawRequest memberWithdrawRequest() {
		return MemberWithdrawRequest.builder()
			.userId("nuribooks95")
			.password("abc123")
			.build();
	}

	private MemberRegisterRequest memberCreateRequest() {
		return MemberRegisterRequest.builder()
			.name("boho")
			.userId("nuribooks95")
			.password("abc123")
			.phoneNumber("042-8282-8282")
			.email("nhnacademy@nuriBooks.com")
			.birthday(LocalDate.of(1988, 8, 12))
			.build();
	}

	private Member member(Customer savedCustomer) {
		return Member.builder()
			.customer_id(savedCustomer)
			.authority(MEMBER)
			.grade(STANDARD)
			.status(ACTIVE)
			.userId("nuribooks95")
			.birthday(LocalDate.of(1988, 8, 12))
			.createdAt(LocalDateTime.now())
			.point(ZERO)
			.totalPaymentAmount(ZERO)
			.build();
	}

	private Customer customer() {
		return Customer.builder()
			.name("boho")
			.password("abc123")
			.phoneNumber("042-8282-8282")
			.email("nhnacademy@nuriBooks.com")
			.build();
	}
}