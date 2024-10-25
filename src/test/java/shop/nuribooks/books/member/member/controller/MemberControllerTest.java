package shop.nuribooks.books.member.member.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.annotation.PostConstruct;
import shop.nuribooks.books.member.member.dto.request.MemberRegisterRequest;
import shop.nuribooks.books.member.member.dto.request.MemberUpdateRequest;
import shop.nuribooks.books.member.member.dto.request.MemberWithdrawRequest;
import shop.nuribooks.books.member.member.dto.response.MemberCheckResponse;
import shop.nuribooks.books.member.member.dto.response.MemberRegisterResponse;
import shop.nuribooks.books.member.member.dto.response.MemberUpdateResponse;
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
		MemberRegisterRequest request = memberRegisterRequest();
		MemberRegisterResponse response = memberRegisterResponse();

		when(memberService.registerMember(any(MemberRegisterRequest.class))).thenReturn(response);

		//when
		ResultActions result = mockMvc.perform(post("/api/member")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)));

		//then
		result.andExpect(status().isCreated())
			.andExpect(jsonPath("name").value(response.getName()))
			.andExpect(jsonPath("userId").value(response.getUserId()))
			.andExpect(jsonPath("phoneNumber").value(response.getPhoneNumber()))
			.andExpect(jsonPath("email").value(response.getEmail()))
			.andExpect(jsonPath("birthday").value(response.getBirthday().toString()));
	}

	// @DisplayName("회원 등록 실패 - 유효하지 않은 요청 데이터")
	// @Test
	// void memberRegister_InvalidRequestData() {
	// 	//given
	// 	MemberRegisterRequest badRequest = badMemberRegisterRequest();
	// }

	@DisplayName("아이디로 회원 이름, 비밀번호, 권한 조회")
	@Test
	void memberCheck() throws Exception {
		//given
		MemberCheckResponse response = memberCheckResponse();
		String requestUserId = "nuribooks";

		when(memberService.checkMember(requestUserId)).thenReturn(response);

		//when
		ResultActions result = mockMvc.perform(get("/api/member/{userId}", requestUserId));

		//then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("name").value(response.getName()))
			.andExpect(jsonPath("password").value(response.getPassword()))
			.andExpect(jsonPath("authority").value(response.getAuthority()));
	}

	@DisplayName("아이디와 비밀번호로 회원 탈퇴")
	@Test
	void memberWithdraw() throws Exception {
		//given
		MemberWithdrawRequest request = memberWithdrawRequest();
		doNothing().when(memberService).withdrawMember(any(MemberWithdrawRequest.class));

		//when
		ResultActions result = mockMvc.perform(patch("/api/member/status")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		//then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("statusCode").value("200"))
			.andExpect(jsonPath("message")
				.value("탈퇴가 성공적으로 완료되었습니다. 귀하의 앞날에 무궁한 발전이 있기를 진심으로 기원하겠습니다."));
	}

	@DisplayName("회원 정보 수정")
	@Test
	void memberUpdate() throws Exception {
		//given
		MemberUpdateRequest request = memberUpdateRequest();
		MemberUpdateResponse response = memberUpdateResponse();
		String requestUserId = "nuribooks";

		// 단일 원시 값에 대해서만 Mockito는 매처 없이도 작동하지만 여러 인자에 대해서는 모든 인자를 매처로 제공해야 함
		when(memberService.updateMember(eq(requestUserId), any(MemberUpdateRequest.class))).thenReturn(response);

		//when
		ResultActions result = mockMvc.perform(post("/api/member/{userId}", requestUserId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		//then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("name").value(response.getName()))
			.andExpect(jsonPath("phoneNumber").value(response.getPhoneNumber()));
	}

	private MemberRegisterRequest memberRegisterRequest() {
		return MemberRegisterRequest.builder()
			.name("boho")
			.userId("nuribooks")
			.password("abc123")
			.phoneNumber("042-8282-8282")
			.email("nhnacademy@nuriBooks.com")
			.birthday(LocalDate.of(1988, 8, 12))
			.build();
	}

	private MemberRegisterResponse memberRegisterResponse() {
		return MemberRegisterResponse.builder()
			.name("boho")
			.userId("nuribooks")
			.phoneNumber("042-8282-8282")
			.email("nhnacademy@nuriBooks.com")
			.birthday(LocalDate.of(1988, 8, 12))
			.build();
	}

	private MemberRegisterRequest badMemberRegisterRequest() {
		return MemberRegisterRequest.builder()
			.name("")
			.userId("a")
			.password(null)
			.phoneNumber("")
			.email("nhnacademy")
			.birthday(null)
			.build();
	}

	private MemberCheckResponse memberCheckResponse() {
		return MemberCheckResponse.builder()
			.name("boho")
			.password("abc123")
			.authority("ROLE_MEMBER")
			.build();
	}

	private MemberWithdrawRequest memberWithdrawRequest() {
		return MemberWithdrawRequest.builder()
			.userId("nuribooks")
			.password("abc123")
			.build();
	}

	private MemberUpdateRequest memberUpdateRequest() {
		return MemberUpdateRequest.builder()
			.name("수정하고 싶은 이름")
			.password("수정하고 싶은 비밀번호")
			.phoneNumber("010-0000-0000")
			.build();
	}

	private MemberUpdateResponse memberUpdateResponse() {
		return MemberUpdateResponse.builder()
			.name("수정된 이름")
			.phoneNumber("010-0000-0000")
			.build();
	}

}