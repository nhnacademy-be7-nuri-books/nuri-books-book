package shop.nuribooks.books.member.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static shop.nuribooks.books.member.member.entity.StatusType.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import shop.nuribooks.books.book.coupon.service.CouponService;
import shop.nuribooks.books.book.point.service.PointHistoryService;
import shop.nuribooks.books.cart.repository.CartRepository;
import shop.nuribooks.books.common.config.QuerydslConfiguration;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;
import shop.nuribooks.books.exception.BadRequestException;
import shop.nuribooks.books.exception.member.CustomerNotFoundException;
import shop.nuribooks.books.exception.member.EmailAlreadyExistsException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.exception.member.PhoneNumberAlreadyExistsException;
import shop.nuribooks.books.exception.member.UsernameAlreadyExistsException;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.customer.repository.CustomerRepository;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.grade.repository.GradeRepository;
import shop.nuribooks.books.member.member.dto.EntityMapper;
import shop.nuribooks.books.member.member.dto.request.MemberPasswordUpdateRequest;
import shop.nuribooks.books.member.member.dto.request.MemberRegisterRequest;
import shop.nuribooks.books.member.member.dto.request.MemberSearchRequest;
import shop.nuribooks.books.member.member.dto.response.MemberAuthInfoResponse;
import shop.nuribooks.books.member.member.dto.response.MemberDetailsResponse;
import shop.nuribooks.books.member.member.dto.response.MemberRegisterResponse;
import shop.nuribooks.books.member.member.dto.response.MemberSearchResponse;
import shop.nuribooks.books.member.member.entity.AuthorityType;
import shop.nuribooks.books.member.member.entity.GenderType;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
@Import(QuerydslConfiguration.class)
class MemberServiceImplTest {

	@InjectMocks
	private MemberServiceImpl memberServiceImpl;

	@Mock
	private GradeRepository gradeRepository;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PointHistoryService pointHistoryService;

	@Mock
	private CartRepository cartRepository;

	@Mock
	private CouponService couponService;

	@Mock
	private ApplicationEventPublisher publisher;

	@BeforeEach
	void setUp() {
		MemberIdContext.setMemberId(1L);
	}

	@AfterEach
	void tearDown() {
		MemberIdContext.clear();
	}

	@DisplayName("회원 등록 성공")
	@Test
	void registerMember() {
		// given
		MemberRegisterRequest request = getMemberCreateRequest();
		Grade standard = getGrade();
		Customer savedCustomer = EntityMapper.toCustomerEntity(request);
		Member savedMember = getSavedMember(savedCustomer);

		when(customerRepository.existsByEmail(request.email())).thenReturn(false);
		when(memberRepository.existsByUsername(request.username())).thenReturn(false);
		when(customerRepository.save(any(Customer.class)))
			.thenReturn(savedCustomer);
		when(gradeRepository.findByName("STANDARD")).thenReturn(Optional.of(standard));
		when(memberRepository.save(any(Member.class)))
			.thenReturn(savedMember);

		// when
		MemberRegisterResponse response = memberServiceImpl.registerMember(request);

		// then
		assertThat(response.username()).isEqualTo(request.username());
		assertThat(response.phoneNumber()).isEqualTo(request.phoneNumber());
		assertThat(response.email()).isEqualTo(request.email());
		assertThat(response.gender()).isEqualTo(request.gender());

		// verify
		verify(customerRepository, times(1)).save(any(Customer.class));
		verify(memberRepository, times(1)).save(any(Member.class));

	}

	@DisplayName("회원 등록 실패 - 중복된 이메일")
	@Test
	void registerMember_EmailAlreadyExists() {
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
	void registerMember_UsernameAlreadyExists() {
		//given
		MemberRegisterRequest request = getMemberCreateRequest();

		when(customerRepository.existsByEmail(request.email())).thenReturn(false);
		when(memberRepository.existsByUsername(request.username())).thenReturn(true);

		//when / then
		UsernameAlreadyExistsException exception = assertThrows(UsernameAlreadyExistsException.class,
			() -> memberServiceImpl.registerMember(request));
		assertThat(exception.getMessage()).isEqualTo("이미 존재하는 아이디입니다.");
	}

	@DisplayName("회원 등록 실패 - 중복된 전화번호")
	@Test
	void registerMember_PhoneNumberAlreadyExists() {
		//given
		MemberRegisterRequest request = getMemberCreateRequest();

		when(customerRepository.existsByEmail(request.email())).thenReturn(false);
		when(customerRepository.existsByPhoneNumber(request.phoneNumber())).thenReturn(true);

		//when / then
		PhoneNumberAlreadyExistsException exception = assertThrows(PhoneNumberAlreadyExistsException.class,
			() -> memberServiceImpl.registerMember(request));
		assertThat(exception.getMessage()).isEqualTo("이미 존재하는 전화번호입니다.");
	}

	@DisplayName("회원 탈퇴 성공")
	@Test
	void withdrawMember() {
		//given
		Long memberId = MemberIdContext.getMemberId();

		Customer existingCustomer = getSavedCustomer();
		Member existingMember = spy(getSavedMember(existingCustomer));

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(existingMember));

		//when
		memberServiceImpl.withdrawMember(memberId);

		//then
		verify(existingMember, times(1)).changeToWithdrawn(); // 메서드 호출 확인
		assertThat(existingMember.getStatus()).isEqualTo(WITHDRAWN); // 상태가 WITHDRAWN로 변경되었는지 확인
		assertThat(existingMember.getWithdrawnAt()).isNotNull(); // withdrawnAt이 현재 시간으로 설정되었는지 확인
	}

	@DisplayName("회원 탈퇴 실패 - 존재하지 않는 회원")
	@Test
	void withdrawMember_UsernameNotFound() {
		//given
		Long memberId = MemberIdContext.getMemberId();

		when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

		//when / then
		MemberNotFoundException exception = assertThrows(MemberNotFoundException.class,
			() -> memberServiceImpl.withdrawMember(memberId));
		assertThat(exception.getMessage()).isEqualTo("존재하지 않는 회원입니다.");
	}

	@DisplayName("회원 탈퇴 실패 - 관리자는 탈퇴할 수 없음")
	@Test
	void withdrawMember_AdminCannotWithdraw() {
		//given
		Long memberId = MemberIdContext.getMemberId();
		Customer savedCustomer = getSavedCustomer();
		Member admin = getAdmin(savedCustomer);

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(admin));

		//when / then
		BadRequestException exception = assertThrows(BadRequestException.class,
			() -> memberServiceImpl.withdrawMember(memberId));
		assertThat(exception.getMessage()).isEqualTo("관리자 등급은 탈퇴할 수 없습니다.");
	}

	@DisplayName("회원 정보 수정 성공")
	@Test
	void updateMember() {
		//given
		Long memberId = MemberIdContext.getMemberId();
		MemberPasswordUpdateRequest request = getMemberUpdateRequest();
		Customer existingCustomer = spy(getSavedCustomer());

		when(customerRepository.findById(memberId)).thenReturn(Optional.of(existingCustomer));

		//when
		memberServiceImpl.updateMember(memberId, request);

		//then
	}

	@DisplayName("회원 정보 수정 실패 - 존재하지 않는 고객")
	@Test
	void updateMember_CustomerNotFound() {
		//given
		Long memberId = MemberIdContext.getMemberId();
		MemberPasswordUpdateRequest request = getMemberUpdateRequest();

		when(customerRepository.findById(memberId)).thenReturn(Optional.empty());

		//when / then
		CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
			() -> memberServiceImpl.updateMember(memberId, request));
		assertThat(exception.getMessage()).isEqualTo("존재하지 않는 고객입니다.");
	}

	@DisplayName("username으로 회원 PK id, username, 비밀번호, 권한 조회 성공")
	@Test
	void getMemberAuthInfoByUsername() {
		//given
		Customer savedCustomer = getSavedCustomer();
		Member savedMember = getSavedMember(savedCustomer);

		when(memberRepository.findByUsername(savedMember.getUsername())).thenReturn(Optional.of(savedMember));
		when(customerRepository.findById(savedMember.getId())).thenReturn(Optional.of(savedCustomer));

		//when
		MemberAuthInfoResponse response = memberServiceImpl.getMemberAuthInfoByUsername(savedMember.getUsername());

		//then
		assertThat(response.customerId()).isEqualTo(savedCustomer.getId());
		assertThat(response.username()).isEqualTo(savedMember.getUsername());
		assertThat(response.password()).isEqualTo(savedCustomer.getPassword());
		assertThat(response.role()).isEqualTo("ROLE_" + savedMember.getAuthority().name());
	}

	@DisplayName("username으로 회원 PK id, username, 비밀번호, 권한 조회 실패 - 회원이 존재하지 않을 때")
	@Test
	void getMemberAuthInfoByUsername_MemberNotFound() {
		//given
		String requestUsername = "nhnacademy";

		when(memberRepository.findByUsername(requestUsername)).thenReturn(Optional.empty());

		//when
		MemberAuthInfoResponse response = memberServiceImpl.getMemberAuthInfoByUsername(requestUsername);

		//then
		assertNotNull(response);
		assertNull(response.customerId());
		assertNull(response.username());
		assertNull(response.password());
		assertNull(response.role());
	}

	@DisplayName("회원 PK id, username, 비밀번호, 권한 조회 실패 - 고객이 존재하지 않을 때")
	@Test
	void getMember_AuthInfo_CustomerNotFound() {
		//given
		Customer existingCustomer = getSavedCustomer();
		Member existingMember = getSavedMember(existingCustomer);
		String requestUserid = "nhnacademy";

		when(memberRepository.findByUsername(requestUserid)).thenReturn(Optional.of(existingMember));
		when(customerRepository.findById(existingMember.getId())).thenReturn(Optional.empty());

		//when
		MemberAuthInfoResponse response = memberServiceImpl.getMemberAuthInfoByUsername(requestUserid);

		//then
		assertNotNull(response);
		assertNull(response.customerId());
		assertNull(response.username());
		assertNull(response.password());
		assertNull(response.role());
	}

	@DisplayName("이메일로 회원 PK id, username, 비밀번호, 권한 조회 성공")
	@Test
	void getMemberAuthInfoByEmail() {
		//given
		String requestEmail = "nhnacademy@nuriBooks.com";
		Customer savedCustomer = getSavedCustomer();
		Member savedMember = getSavedMember(savedCustomer);

		when(customerRepository.findByEmail(requestEmail)).thenReturn(Optional.of(savedCustomer));
		when(memberRepository.findById(savedCustomer.getId())).thenReturn(Optional.of(savedMember));

		//when
		MemberAuthInfoResponse response = memberServiceImpl.getMemberAuthInfoByEmail(requestEmail);

		//then
		assertThat(response.customerId()).isEqualTo(savedCustomer.getId());
		assertThat(response.username()).isEqualTo(savedMember.getUsername());
		assertThat(response.password()).isEqualTo(savedCustomer.getPassword());
		assertThat(response.role()).isEqualTo("ROLE_" + savedMember.getAuthority().name());
	}

	@DisplayName("이메일로 회원 PK id, username, 비밀번호, 권한 조회 실패 - 고객이 존재하지 않을 때")
	@Test
	void getMemberAuthInfoByEmail_CustomerNotFound() {
		//given
		String requestEmail = "nhnacademy@nuriBooks.com";

		when(customerRepository.findByEmail(requestEmail)).thenReturn(Optional.empty());

		//when
		MemberAuthInfoResponse response = memberServiceImpl.getMemberAuthInfoByEmail(requestEmail);

		//then
		assertNotNull(response);
		assertNull(response.customerId());
		assertNull(response.username());
		assertNull(response.password());
		assertNull(response.role());
	}

	@DisplayName("이메일로 회원 PK id, username, 비밀번호, 권한 조회 실패 - 회원이 존재하지 않을 때")
	@Test
	void getMemberAuthInfoByEmail_MemberNotFound() {
		//given
		String requestEmail = "nhnacademy@nuriBooks.com";
		Customer savedCustomer = getSavedCustomer();

		when(customerRepository.findByEmail(requestEmail)).thenReturn(Optional.of(savedCustomer));
		when(memberRepository.findById(savedCustomer.getId())).thenReturn(Optional.empty());

		//when
		MemberAuthInfoResponse response = memberServiceImpl.getMemberAuthInfoByEmail(requestEmail);

		//then
		assertNotNull(response);
		assertNull(response.customerId());
		assertNull(response.username());
		assertNull(response.password());
		assertNull(response.role());
	}

	@DisplayName("회원 상세 조회 성공")
	@Test
	void getMemberDetails() {
		//given
		Long memberId = MemberIdContext.getMemberId();
		Customer savedCustomer = getSavedCustomer();
		Member savedMember = getSavedMember(savedCustomer);

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(savedMember));
		when(customerRepository.findById(memberId)).thenReturn(Optional.of(savedCustomer));

		//when
		MemberDetailsResponse response = memberServiceImpl.getMemberDetails(memberId);

		//then
		assertThat(response.name()).isEqualTo(savedCustomer.getName());
		assertThat(response.username()).isEqualTo(savedMember.getUsername());
		assertThat(response.phoneNumber()).isEqualTo(savedCustomer.getPhoneNumber());
		assertThat(response.email()).isEqualTo(savedCustomer.getEmail());
		assertThat(response.point()).isEqualTo(savedMember.getPoint());
		assertThat(response.totalPaymentAmount()).isEqualTo(savedMember.getTotalPaymentAmount());
		assertThat(response.gradeName()).isEqualTo(savedMember.getGrade().getName());
		assertThat(response.pointRate()).isEqualTo(savedMember.getGrade().getPointRate());
		assertThat(response.createdAt()).isEqualTo(savedMember.getCreatedAt());
	}

	@DisplayName("회원 상세 조회 실패 - 회원이 존재하지 않을 때")
	@Test
	void getMemberDetails_MemberNotFound() {
		//given
		Long notExistMemberId = 24851L;

		when(memberRepository.findById(notExistMemberId)).thenReturn(Optional.empty());

		//when / then
		assertThatThrownBy(() -> memberServiceImpl.getMemberDetails(notExistMemberId))
			.isInstanceOf(MemberNotFoundException.class)
			.hasMessage("존재하지 않는 회원입니다.");
	}

	@DisplayName("회원 상세 조회 실패 - 고객이 존재하지 않을 때")
	@Test
	void getMemberDetails_CustomerNotFound() {
		//given
		Long notExistMemberId = 24851L;
		Customer savedCustomer = getSavedCustomer();
		Member savedMember = getSavedMember(savedCustomer);

		when(memberRepository.findById(notExistMemberId)).thenReturn(Optional.of(savedMember));
		when(customerRepository.findById(notExistMemberId)).thenReturn(Optional.empty());

		//when / then
		assertThatThrownBy(() -> memberServiceImpl.getMemberDetails(notExistMemberId))
			.isInstanceOf(CustomerNotFoundException.class)
			.hasMessage("존재하지 않는 고객입니다.");
	}

	@DisplayName("회원 username으로 최근 로그인 시간 업데이트 성공")
	@Test
	void updateMemberLatestLoginAt() {
		//given
		String username = "nuribooks95";
		Customer savedCustomer = getSavedCustomer();
		Member existingMember = spy(getSavedMember(savedCustomer));

		when(memberRepository.findByUsername(username)).thenReturn(Optional.of(existingMember));

		//when
		memberServiceImpl.updateMemberLatestLoginAt(username);

		//then
		verify(existingMember, times(1)).updateLatestLoginAt(); // 메서드 호출 확인
		assertThat(existingMember.getLatestLoginAt()).isNotNull(); // withdrawnAt이 현재 시간으로 설정되었는지 확인
	}

	@DisplayName("회원 username으로 최근 로그인 시간 업데이트 실패 - 존재하지 않는 회원")
	@Test
	void updateMemberLatestLoginAt_memberNotFound() {
		//given
		String username = "nuribooks95";

		when(memberRepository.findByUsername(username)).thenReturn(Optional.empty());

		//when/then
		assertThatThrownBy(() -> memberServiceImpl.updateMemberLatestLoginAt(username))
			.isInstanceOf(MemberNotFoundException.class)
			.hasMessage("존재하지 않는 회원입니다.");
	}

	@DisplayName("회원 username으로 휴면 상태 해제 성공")
	@Test
	void reactiveMember() {
		//given
		String username = "nuribooks95";
		Customer savedCustomer = getSavedCustomer();
		Member existingMember = spy(getSavedMember(savedCustomer));

		when(memberRepository.findByUsername(username)).thenReturn(Optional.of(existingMember));

		//when
		memberServiceImpl.reactiveMember(username);

		//then
		verify(existingMember, times(1)).reactiveMemberStatus(); // 메서드 호출 확인
		assertThat(existingMember.getStatus()).isEqualTo(ACTIVE); // withdrawnAt이 현재 시간으로 설정되었는지 확인
	}

	@DisplayName("회원 username으로 휴면 상태 해제 실패 - 존재하지 않는 회원")
	@Test
	void reactiveMember_memberNotFound() {
		//given
		String username = "nuribooks95";

		when(memberRepository.findByUsername(username)).thenReturn(Optional.empty());

		//when/then
		assertThatThrownBy(() -> memberServiceImpl.reactiveMember(username))
			.isInstanceOf(MemberNotFoundException.class)
			.hasMessage("존재하지 않는 회원입니다.");
	}

	@DisplayName("관리자가 다양한 검색 조건을 이용하여 회원 목록 조회")
	@Test
	void searchMembersWithPaging() {
		//given
		MemberSearchRequest searchRequest = MemberSearchRequest.builder()
			.name("nuri")
			.build();

		List<MemberSearchResponse> searchList = List.of(MemberSearchResponse.builder()
			.name("nuri")
			.build());

		PageRequest pageRequest = PageRequest.of(0, 20);
		Page<MemberSearchResponse> searchPage = new PageImpl<>(searchList, pageRequest, searchList.size());

		when(memberRepository.searchMembersWithPaging(searchRequest, pageRequest)).thenReturn(searchPage);

		//when
		Page<MemberSearchResponse> result =
			memberServiceImpl.searchMembersWithPaging(searchRequest, pageRequest);

		//then
		assertThat(result.getSize()).isEqualTo(searchPage.getSize());
	}

	/**
	 * 테스트를 위한 MemberRegisterRequest 생성
	 */
	private MemberRegisterRequest getMemberCreateRequest() {
		return MemberRegisterRequest.builder()
			.name("boho")
			.gender(GenderType.MALE)
			.username("nuribooks95")
			.password("abc123")
			.phoneNumber("042-8282-8282")
			.email("nhnacademy@nuriBooks.com")
			.birthday(LocalDate.of(1988, 8, 12))
			.build();
	}

	/**
	 * 테스트를 위한 MemberPasswordUpdateRequest 생성
	 */
	private MemberPasswordUpdateRequest getMemberUpdateRequest() {
		return MemberPasswordUpdateRequest.builder()
			.password("수정된 비밀번호")
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
	private Customer getSavedCustomer() {
		return Customer.builder()
			.id(1L)
			.name("boho")
			.password("abc123")
			.phoneNumber("01082828282")
			.email("nhnacademy@nuriBooks.com")
			.build();
	}

	/**
	 * 테스트를 위한 회원 생성
	 */
	private Member getSavedMember(Customer savedCustomer) {
		return Member.builder()
			.customer(savedCustomer)
			.authority(AuthorityType.MEMBER)
			.grade(getGrade())
			.status(ACTIVE)
			.gender(GenderType.MALE)
			.username("nuribooks95")
			.birthday(LocalDate.of(1988, 8, 12))
			.createdAt(LocalDateTime.now())
			.point(BigDecimal.ZERO)
			.totalPaymentAmount(BigDecimal.ZERO)
			.build();
	}

	/**
	 * 테스트를 위한 관리자 생성
	 */
	private Member getAdmin(Customer savedCustomer) {
		return Member.builder()
			.customer(savedCustomer)
			.authority(AuthorityType.ADMIN)
			.grade(getGrade())
			.status(ACTIVE)
			.gender(GenderType.MALE)
			.username("nuribooks95")
			.birthday(LocalDate.of(1988, 8, 12))
			.createdAt(LocalDateTime.now())
			.point(BigDecimal.ZERO)
			.totalPaymentAmount(BigDecimal.ZERO)
			.build();
	}
}
