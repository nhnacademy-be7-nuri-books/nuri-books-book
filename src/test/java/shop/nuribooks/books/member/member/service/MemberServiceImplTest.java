package shop.nuribooks.books.member.member.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import shop.nuribooks.books.book.coupon.service.CouponService;
import shop.nuribooks.books.book.point.entity.PointHistory;
import shop.nuribooks.books.book.point.service.PointHistoryService;
import shop.nuribooks.books.cart.repository.CartRepository;
import shop.nuribooks.books.common.config.QuerydslConfiguration;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;
import shop.nuribooks.books.exception.member.CustomerNotFoundException;
import shop.nuribooks.books.exception.member.EmailAlreadyExistsException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.exception.member.UsernameAlreadyExistsException;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.customer.repository.CustomerRepository;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.grade.repository.GradeRepository;
import shop.nuribooks.books.member.member.dto.EntityMapper;
import shop.nuribooks.books.member.member.dto.request.MemberPasswordUpdateRequest;
import shop.nuribooks.books.member.member.dto.request.MemberRegisterRequest;
import shop.nuribooks.books.member.member.dto.response.MemberAuthInfoResponse;
import shop.nuribooks.books.member.member.dto.response.MemberDetailsResponse;
import shop.nuribooks.books.member.member.dto.response.MemberRegisterResponse;
import shop.nuribooks.books.member.member.entity.AuthorityType;
import shop.nuribooks.books.member.member.entity.GenderType;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.entity.StatusType;
import shop.nuribooks.books.member.member.repository.MemberRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        when(pointHistoryService.registerPointHistory(any(), any())).thenReturn(new PointHistory());

        doNothing().when(couponService).issueWelcomeCoupon(any(Member.class));
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
        verify(couponService, times(1)).issueWelcomeCoupon(any(Member.class));  // couponService 호출 확인

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
        assertThat(existingMember.getStatus()).isEqualTo(StatusType.WITHDRAWN); // 상태가 WITHDRAWN로 변경되었는지 확인
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
        verify(existingCustomer, times(1))
                .changeCustomerPassword(request.password());
        assertThat(existingCustomer.getPassword()).isEqualTo(request.password());
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
                .phoneNumber("042-8282-8282")
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
                .status(StatusType.ACTIVE)
                .gender(GenderType.MALE)
                .username("nuribooks95")
                .birthday(LocalDate.of(1988, 8, 12))
                .createdAt(LocalDateTime.now())
                .point(BigDecimal.ZERO)
                .totalPaymentAmount(BigDecimal.ZERO)
                .build();
    }
}
