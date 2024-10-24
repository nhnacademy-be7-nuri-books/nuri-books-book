package shop.nuribooks.books.controller.member;

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
import shop.nuribooks.books.dto.member.request.MemberRegisterRequest;
import shop.nuribooks.books.dto.member.response.MemberCheckResponse;
import shop.nuribooks.books.dto.member.response.MemberRegisterResponse;
import shop.nuribooks.books.service.member.MemberService;

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

	@DisplayName("회원 등록")
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

	private MemberCheckResponse memberCheckResponse() {
		return MemberCheckResponse.builder()
			.name("boho")
			.password("abc123")
			.authority("ROLE_MEMBER")
			.build();
	}

}