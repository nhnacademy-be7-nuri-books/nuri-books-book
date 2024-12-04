package shop.nuribooks.books.member.member.controller;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.annotation.PostConstruct;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.member.dto.request.MemberPasswordUpdateRequest;
import shop.nuribooks.books.member.member.dto.request.MemberRegisterRequest;
import shop.nuribooks.books.member.member.dto.request.MemberSearchRequest;
import shop.nuribooks.books.member.member.dto.response.MemberAuthInfoResponse;
import shop.nuribooks.books.member.member.dto.response.MemberDetailsResponse;
import shop.nuribooks.books.member.member.dto.response.MemberRegisterResponse;
import shop.nuribooks.books.member.member.dto.response.MemberSearchResponse;
import shop.nuribooks.books.member.member.entity.AuthorityType;
import shop.nuribooks.books.member.member.entity.GenderType;
import shop.nuribooks.books.member.member.entity.StatusType;
import shop.nuribooks.books.member.member.service.MemberService;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

	@MockBean
	protected MemberService memberService;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@PostConstruct
	public void init() {
		objectMapper.registerModule(new JavaTimeModule());
	}

	@BeforeEach
	public void setUp() {
		MemberIdContext.setMemberId(1L);
	}

	@AfterEach
	void tearDown() {
		MemberIdContext.clear();
	}

	@DisplayName("회원 등록 성공")
	@Test
	void memberRegister() throws Exception {
		//given
		MemberRegisterRequest request = getMemberRegisterRequest();
		MemberRegisterResponse response = getMemberRegisterResponse();

		when(memberService.registerMember(any(MemberRegisterRequest.class))).thenReturn(response);

		//when
		ResultActions result = mockMvc.perform(post("/api/members")
			.contentType(APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		//then
		result.andExpect(status().isCreated())
			.andExpect(jsonPath("name").value(response.name()))
			.andExpect(jsonPath("gender").value(response.gender().name()))
			.andExpect(jsonPath("username").value(response.username()))
			.andExpect(jsonPath("phoneNumber").value(response.phoneNumber()))
			.andExpect(jsonPath("email").value(response.email()))
			.andExpect(jsonPath("birthday").value(response.birthday().toString()));
	}

	@DisplayName("회원 등록 실패 - validation 에러")
	@Test
	void memberRegister_InvalidRequest() throws Exception {
		//given
		MemberRegisterRequest badRequest = getBadMemberRegisterRequest();

		//when
		ResultActions badResult = mockMvc.perform(post("/api/members")
			.contentType(APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(badRequest)));

		//then
		badResult.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("이름은 반드시 입력해야 합니다.")))
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("성별은 반드시 입력해야 합니다.")))
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("비밀번호는 반드시 입력해야 합니다.")))
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("전화번호는 '-' 없이 '010'으로 시작하는 11자리의 숫자로 입력해야 합니다.")))
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("유효한 이메일 형식으로 입력해야 합니다.")))
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("생일은 반드시 입력해야 합니다.")));
	}

	@DisplayName("username으로 회원 PK id, username, 비밀번호, 권한 조회 성공")
	@Test
	void getMemberAuthInfoByUsername() throws Exception {
		//given
		MemberAuthInfoResponse response = getGetMemberAuthInfoResponse();
		String username = "nuribooks";

		when(memberService.getMemberAuthInfoByUsername(username)).thenReturn(response);

		//when
		ResultActions result = mockMvc.perform(get("/api/members/username/{username}", username));

		//then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("customerId").value(response.customerId()))
			.andExpect(jsonPath("username").value(response.username()))
			.andExpect(jsonPath("password").value(response.password()))
			.andExpect(jsonPath("role").value(response.role()));
	}

	@DisplayName("이메일로 회원 PK id, username, 비밀번호, 권한 조회 성공")
	@Test
	void getMemberAuthInfoByEmail() throws Exception {
		//given
		MemberAuthInfoResponse response = getGetMemberAuthInfoResponse();
		String email = "nuribooks@nhnacademy.com";

		when(memberService.getMemberAuthInfoByEmail(email)).thenReturn(response);

		//when
		ResultActions result = mockMvc.perform(get("/api/members/email/{email}", email));

		//then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("customerId").value(response.customerId()))
			.andExpect(jsonPath("username").value(response.username()))
			.andExpect(jsonPath("password").value(response.password()))
			.andExpect(jsonPath("role").value(response.role()));
	}

	@DisplayName("회원 PK id로 회원 상세 조회 성공")
	@Test
	void getMemberDetails() throws Exception {
		//given
		Long memberId = MemberIdContext.getMemberId();
		MemberDetailsResponse response = getMemberDetailsResponse();

		when(memberService.getMemberDetails(memberId)).thenReturn(response);

		//when
		ResultActions result = mockMvc.perform(get("/api/members/me"));

		//then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("username").value(response.username()))
			.andExpect(jsonPath("name").value(response.name()))
			.andExpect(jsonPath("phoneNumber").value(response.phoneNumber()))
			.andExpect(jsonPath("email").value(response.email()))
			.andExpect(jsonPath("point").value(response.point()))
			.andExpect(jsonPath("totalPaymentAmount").value(response.totalPaymentAmount()))
			.andExpect(jsonPath("gradeName").value(response.gradeName()))
			.andExpect(jsonPath("pointRate").value(response.pointRate()))
			.andExpect(jsonPath("createdAt").value(response.createdAt().toString()));
	}

	@DisplayName("회원 PK id로 회원 탈퇴 성공")
	@Test
	void memberWithdraw() throws Exception {
		//given
		doNothing().when(memberService).withdrawMember(anyLong());

		//when
		ResultActions result = mockMvc.perform(delete("/api/members/me"));

		//then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("statusCode").value("200"))
			.andExpect(jsonPath("message")
				.value("탈퇴가 성공적으로 완료되었습니다. 귀하의 앞날에 무궁한 발전이 있기를 진심으로 기원하겠습니다."));
	}

	@DisplayName("회원 PK id로 회원 정보 수정 성공")
	@Test
	void memberUpdate() throws Exception {
		//given
		Long memberId = MemberIdContext.getMemberId();
		MemberPasswordUpdateRequest request = getMemberUpdateRequest();

		doNothing().when(memberService).updateMember(eq(memberId), any(MemberPasswordUpdateRequest.class));

		//when
		ResultActions result = mockMvc.perform(put("/api/members/me")
			.contentType(APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		//then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("statusCode").value("200"))
			.andExpect(jsonPath("message")
				.value("회원 정보가 수정되었습니다."));
	}

	@DisplayName("회원 정보 수정 실패 - validation 에러")
	@Test
	void memberUpdate_invalidRequest() throws Exception {
		//given
		MemberPasswordUpdateRequest badRequest = getBadMemberUpdateRequest();

		//when
		ResultActions badResult = mockMvc.perform(put("/api/members/me")
			.contentType(APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(badRequest)));

		//then
		badResult.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("비밀번호는 반드시 입력해야 합니다.")));
	}

	@DisplayName("회원 username으로 최근 로그인 시간 업데이트")
	@Test
	void memberLatestLoginAtUpdate() throws Exception {
		//given
		String username = "member50";

		doNothing().when(memberService).updateMemberLatestLoginAt(username);

		//when
		ResultActions result = mockMvc.perform(put("/api/members/{username}/login-time", username));

		//then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("statusCode").value("200"))
			.andExpect(jsonPath("message")
				.value("최근 로그인 시간이 수정되었습니다."));
	}

	@DisplayName("회원 username으로 휴면 해제")
	@Test
	void memberReactive() throws Exception {
		//given
		String username = "member50";

		doNothing().when(memberService).reactiveMember(username);

		//when
		ResultActions result = mockMvc.perform(put("/api/members/{username}/active", username));

		//then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("statusCode").value("200"))
			.andExpect(jsonPath("message")
				.value("회원의 휴면 상태가 해제되었습니다."));
	}

	@DisplayName("관리자가 다양한 검색 조건을 이용하여 회원 목록 조회")
	@Test
	void memberSearchWithPaging() throws Exception {
		//given
		MemberSearchResponse response = getMemberSearchResponse();
		Page<MemberSearchResponse> pageResponse = new PageImpl<>(List.of(response));

		when(memberService.searchMembersWithPaging(any(MemberSearchRequest.class), any(Pageable.class)))
			.thenReturn(pageResponse);

		//when
		ResultActions result = mockMvc.perform(get("/api/members")
			.param("name", "김")
			.param("email", "nuri")
			.param("phoneNumber", "010")
			.param("gender", "MALE")
			.param("status", "ACTIVE")
			.param("page", "0") // Pageable에 필요한 page 파라미터
			.param("size", "10")); // Pageable에 필요한 size 파라미터

		//then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].customerId").value(response.customerId()))
			.andExpect(jsonPath("$.content[0].username").value(response.username()))
			.andExpect(jsonPath("$.content[0].email").value(response.email()))
			.andExpect(jsonPath("$.content.length()").value(1));
	}

	/**
	 * 테스트를 위한 MemberRegisterRequest 생성
	 */
	private MemberRegisterRequest getMemberRegisterRequest() {
		return MemberRegisterRequest.builder()
			.name("boho")
			.gender(GenderType.MALE)
			.username("nuribooks1")
			.password("abc123!@#")
			.phoneNumber("01082828282")
			.email("nhnacademy@nuriBooks.com")
			.birthday(LocalDate.of(1988, 8, 12))
			.build();
	}

	/**
	 * 테스트를 위한 MemberRegisterResponse 생성
	 */
	private MemberRegisterResponse getMemberRegisterResponse() {
		return MemberRegisterResponse.builder()
			.name("boho")
			.gender(GenderType.MALE)
			.username("nuribooks")
			.phoneNumber("042-8282-8282")
			.email("nhnacademy@nuriBooks.com")
			.birthday(LocalDate.of(1988, 8, 12))
			.build();
	}

	/**
	 * 테스트를 위한 잘못된 MemberRegisterRequest 생성
	 */
	private MemberRegisterRequest getBadMemberRegisterRequest() {
		return MemberRegisterRequest.builder()
			.name("  ")
			.gender(null)
			.username("a")
			.password(null)
			.phoneNumber("")
			.email("nhnacademy")
			.birthday(null)
			.build();
	}

	/**
	 * 테스트를 위한 MemberAuthInfoResponse 생성
	 */
	private MemberAuthInfoResponse getGetMemberAuthInfoResponse() {
		return MemberAuthInfoResponse.builder()
			.customerId(1L)
			.username("nuribooks")
			.password("abc123")
			.role("ROLE_MEMBER")
			.build();
	}

	/**
	 * 테스트를 위한 MemberDetailsResponse 생성
	 */
	private MemberDetailsResponse getMemberDetailsResponse() {
		return MemberDetailsResponse.builder()
			.username("abcd1234")
			.name("boho")
			.phoneNumber("01082828282")
			.email("boho@nhnacademy.com")
			.point(BigDecimal.ZERO)
			.totalPaymentAmount(BigDecimal.ZERO)
			.gradeName(getStandardGrade().getName())
			.pointRate((getStandardGrade().getPointRate()))
			.createdAt(LocalDateTime.of(2020, 2, 22, 22, 22, 22))
			.build();
	}

	/**
	 * 테스트를 위한 등급 생성
	 */
	private Grade getStandardGrade() {
		return Grade.builder()
			.id(1)
			.name("STANDARD")
			.pointRate(3)
			.requirement(BigDecimal.valueOf(100_000))
			.build();
	}

	/**
	 * 테스트를 위한 MemberPasswordUpdateRequest 생성
	 */
	private MemberPasswordUpdateRequest getMemberUpdateRequest() {
		return MemberPasswordUpdateRequest.builder()
			.password("수정하고 싶은 비밀번호")
			.build();
	}

	/**
	 * 테스트를 위한 잘못된 MemberPasswordUpdateRequest 생성
	 */
	private MemberPasswordUpdateRequest getBadMemberUpdateRequest() {
		return MemberPasswordUpdateRequest.builder()
			.password("   ")
			.build();
	}

	/**
	 * 테스트를 위한 MemberSearchResponse 생성
	 */
	private MemberSearchResponse getMemberSearchResponse() {
		return MemberSearchResponse.builder()
			.customerId(1L)
			.name("곽홍섭")
			.gender(GenderType.MALE)
			.phoneNumber("01082828282")
			.email("hongsup@nhnacademy.com")
			.birthday(LocalDate.of(1987, 11, 28))
			.username("abcd1234")
			.point(BigDecimal.ZERO)
			.totalPaymentAmount(BigDecimal.ZERO)
			.authority(AuthorityType.MEMBER)
			.gradeName("STANDARD")
			.status(StatusType.ACTIVE)
			.createdAt(LocalDateTime.now())
			.latestLoginAt(null)
			.build();
	}
}
