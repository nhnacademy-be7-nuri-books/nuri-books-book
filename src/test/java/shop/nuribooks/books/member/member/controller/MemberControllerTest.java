package shop.nuribooks.books.member.member.controller;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.annotation.PostConstruct;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.member.dto.request.MemberDetailsRequest;
import shop.nuribooks.books.member.member.dto.request.MemberRegisterRequest;
import shop.nuribooks.books.member.member.dto.request.MemberUpdateRequest;
import shop.nuribooks.books.member.member.dto.request.MemberWithdrawRequest;
import shop.nuribooks.books.member.member.dto.response.MemberAuthInfoResponse;
import shop.nuribooks.books.member.member.dto.response.MemberDetailsResponse;
import shop.nuribooks.books.member.member.dto.response.MemberRegisterResponse;
import shop.nuribooks.books.member.member.dto.response.MemberUpdateResponse;
import shop.nuribooks.books.member.member.entity.AuthorityType;
import shop.nuribooks.books.member.member.entity.GenderType;
import shop.nuribooks.books.member.member.entity.StatusType;
import shop.nuribooks.books.member.member.service.MemberService;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

	@MockBean
	private MemberService memberService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@PostConstruct
	public void init() {
		objectMapper.registerModule(new JavaTimeModule());
	}

	@DisplayName("회원 등록 성공")
	@Test
	void memberRegister() throws Exception {
		//given
		MemberRegisterRequest request = getMemberRegisterRequest();
		MemberRegisterResponse response = getMemberRegisterResponse();

		when(memberService.registerMember(any(MemberRegisterRequest.class))).thenReturn(response);

		//when
		ResultActions result = mockMvc.perform(post("/api/member")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)));

		//then
		result.andExpect(status().isCreated())
			.andExpect(jsonPath("name").value(response.name()))
			.andExpect(jsonPath("gender").value(response.gender().name()))
			.andExpect(jsonPath("userId").value(response.userId()))
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
		ResultActions badResult = mockMvc.perform(post("/api/member")
			.contentType(APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(badRequest)));

		//then
		badResult.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("이름은 반드시 입력해야 합니다.")))
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("성별은 반드시 입력해야 합니다.")))
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("아이디는 반드시 8자 이상 20자 이하로 입력해야 합니다.")))
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("비밀번호는 반드시 입력해야 합니다.")))
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("전화번호는 반드시 입력해야 합니다.")))
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("유효한 이메일 형식으로 입력해야 합니다.")))
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("생일은 반드시 입력해야 합니다.")));
	}

	@DisplayName("아이디로 회원 이름, 비밀번호, 권한 조회 성공")
	@Test
	void getMemberAuthInfo() throws Exception {
		//given
		MemberAuthInfoResponse response = getGetMemberAuthInfoResponse();
		String requestUserId = "nuribooks";

		when(memberService.getMemberAuthInfo(requestUserId)).thenReturn(response);

		//when
		ResultActions result = mockMvc.perform(get("/api/member/{userId}", requestUserId));

		//then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("name").value(response.name()))
			.andExpect(jsonPath("password").value(response.password()))
			.andExpect(jsonPath("authority").value(response.authority()));
	}

	@DisplayName("아이디로 비밀번호로 회원 상세 조회 성공")
	@Test
	void getMemberDetails() throws Exception {
		//given
		MemberDetailsRequest request = getMemberDetailsRequest();
		MemberDetailsResponse response = getMemberDetailsResponse();

		when(memberService.getMemberDetails(request)).thenReturn(response);

		//when
		ResultActions result = mockMvc.perform(get("/api/member")
			.contentType(APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		//then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("name").value(response.name()))
			.andExpect(jsonPath("gender").value(response.gender().name()))
			.andExpect(jsonPath("phoneNumber").value(response.phoneNumber()))
			.andExpect(jsonPath("email").value(response.email()))
			.andExpect(jsonPath("birthday").value(response.birthday().toString()))
			.andExpect(jsonPath("userId").value(response.userId()))
			.andExpect(jsonPath("password").value(response.password()))
			.andExpect(jsonPath("point").value(response.point()))
			.andExpect(jsonPath("totalPaymentAmount").value(response.totalPaymentAmount()))
			.andExpect(jsonPath("authority").value(response.authority().name()))
			.andExpect(jsonPath("grade.id").value(response.grade().getId()))
			.andExpect(jsonPath("grade.name").value(response.grade().getName()))
			.andExpect(jsonPath("grade.pointRate").value(response.grade().getPointRate()))
			.andExpect(jsonPath("grade.requirement").value(response.grade().getRequirement()))
			.andExpect(jsonPath("status").value(response.status().name()))
			.andExpect(jsonPath("createdAt").value(response.createdAt().toString()))
			.andExpect(jsonPath("latestLoginAt").value(response.latestLoginAt().toString()));
	}

	@DisplayName("아이디와 비밀번호로 회원 탈퇴 성공")
	@Test
	void memberWithdraw() throws Exception {
		//given
		MemberWithdrawRequest request = getMemberWithdrawRequest();
		doNothing().when(memberService).withdrawMember(any(MemberWithdrawRequest.class));

		//when
		ResultActions result = mockMvc.perform(delete("/api/member")
			.contentType(APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		//then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("statusCode").value("200"))
			.andExpect(jsonPath("message")
				.value("탈퇴가 성공적으로 완료되었습니다. 귀하의 앞날에 무궁한 발전이 있기를 진심으로 기원하겠습니다."));
	}

	@DisplayName("회원 정보 수정 성공")
	@Test
	void memberUpdate() throws Exception {
		//given
		MemberUpdateRequest request = getMemberUpdateRequest();
		MemberUpdateResponse response = getMemberUpdateResponse();
		String requestUserId = "nuribooks";

		// 단일 원시 값에 대해서만 Mockito는 매처 없이도 작동하지만 여러 인자에 대해서는 모든 인자를 매처로 제공해야 함
		when(memberService.updateMember(eq(requestUserId), any(MemberUpdateRequest.class))).thenReturn(response);

		//when
		ResultActions result = mockMvc.perform(post("/api/member/{userId}", requestUserId)
			.contentType(APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		//then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("name").value(response.name()))
			.andExpect(jsonPath("phoneNumber").value(response.phoneNumber()));
	}

	@DisplayName("회원 정보 수정 실패 - validation 에러")
	@Test
	void memberUpdate_invalidRequest() throws Exception {
	    //given
		MemberUpdateRequest badRequest = getBadMemberUpdateRequest();
		String requiredUserId = "nuribooks";

		//when
		ResultActions badResult = mockMvc.perform(post("/api/member/{userId}", requiredUserId)
			.contentType(APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(badRequest)));

		//then
		badResult.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("이름은 반드시 입력해야 합니다.")))
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("비밀번호는 반드시 입력해야 합니다.")))
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("전화번호는 반드시 입력해야 합니다.")));
	}


	/**
	 * 테스트를 위한 MemberRegisterRequest 생성
	 */
	private MemberRegisterRequest getMemberRegisterRequest() {
		return MemberRegisterRequest.builder()
			.name("boho")
			.gender(GenderType.MALE)
			.userId("nuribooks")
			.password("abc123")
			.phoneNumber("042-8282-8282")
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
			.userId("nuribooks")
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
			.userId("a")
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
			.name("boho")
			.password("abc123")
			.authority("ROLE_MEMBER")
			.build();
	}

	/**
	 * 테스트를 위한 MemberDetailsRequest 생성
	 */
	private MemberDetailsRequest getMemberDetailsRequest() {
		return MemberDetailsRequest.builder()
			.userId("nuribooks95")
			.password("abc123")
			.build();
	}

	/**
	 * 테스트를 위한 MemberDetailsResponse 생성
	 */
	private MemberDetailsResponse getMemberDetailsResponse() {
		return MemberDetailsResponse.builder()
			.name("boho")
			.gender(GenderType.MALE)
			.phoneNumber("042-8282-8282")
			.email("boho@nhnacademy.com")
			.birthday(LocalDate.of(2000, 2, 22))
			.userId("nuribooks")
			.password("abc123")
			.point(BigDecimal.ZERO)
			.totalPaymentAmount(BigDecimal.ZERO)
			.authority(AuthorityType.MEMBER)
			.grade(getStandardGrade())
			.status(StatusType.ACTIVE)
			.createdAt(LocalDateTime.of(2020, 2, 22, 22, 22 ,22))
			.latestLoginAt(LocalDateTime.of(2022,2,22,22,22,22))
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
	 * 테스트를 위한 MemberWithdrawRequest 생성
	 */
	private MemberWithdrawRequest getMemberWithdrawRequest() {
		return MemberWithdrawRequest.builder()
			.userId("nuribooks")
			.password("abc123")
			.build();
	}

	/**
	 * 테스트를 위한 MemberUpdateRequest 생성
	 */
	private MemberUpdateRequest getMemberUpdateRequest() {
		return MemberUpdateRequest.builder()
			.name("수정하고 싶은 이름")
			.password("수정하고 싶은 비밀번호")
			.phoneNumber("010-0000-0000")
			.build();
	}

	/**
	 * 테스트를 위한 MemberUpdateResponse 생성
	 */
	private MemberUpdateResponse getMemberUpdateResponse() {
		return MemberUpdateResponse.builder()
			.name("수정된 이름")
			.phoneNumber("010-0000-0000")
			.build();
	}

	/**
	 * 테스트를 위한 잘못된 MemberUpdateRequest 생성
	 */
	private MemberUpdateRequest getBadMemberUpdateRequest() {
		return MemberUpdateRequest.builder()
			.name("   ")
			.password("   ")
			.phoneNumber(null)
			.build();
	}

}