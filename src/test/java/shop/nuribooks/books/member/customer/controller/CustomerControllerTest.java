package shop.nuribooks.books.member.customer.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.common.ControllerTestSupport;
import shop.nuribooks.books.member.customer.dto.request.CustomerRegisterRequest;
import shop.nuribooks.books.member.customer.dto.response.CustomerRegisterResponse;
import shop.nuribooks.books.member.customer.service.CustomerService;

public class CustomerControllerTest extends ControllerTestSupport {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@DisplayName("비회원 등록 성공")
	@Test
	void customerRegister() throws Exception {
	    //given
		CustomerRegisterRequest request = getCustomerRegisterRequest();
		CustomerRegisterResponse response = getCustomerRegisterResponse();

		when(customerService.registerCustomer(any(CustomerRegisterRequest.class))).thenReturn(response);

		//when
		ResultActions result = mockMvc.perform(post("/api/members/customers")
			.contentType(APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

	    //then
		result.andExpect(status().isCreated())
			.andExpect(jsonPath("name").value(response.name()))
			.andExpect(jsonPath("phoneNumber").value(response.phoneNumber()))
			.andExpect(jsonPath("email").value(response.email()));
	}

	@DisplayName("비회원 등록 실패 - validation 에러")
	@Test
	void customerRegister_invalidRequest() throws Exception {
		//given
		CustomerRegisterRequest badRequest = getBadCustomerRegisterRequest();

		//when
		ResultActions badResult = mockMvc.perform(post("/api/members/customers")
			.contentType(APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(badRequest)));

		//then
		badResult.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("이름은 반드시 입력해야 합니다.")))
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("비밀번호는 반드시 입력해야 합니다.")))
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("전화번호는 반드시 입력해야 합니다.")))
			.andExpect(jsonPath("$.message")
				.value(Matchers.containsString("유효한 이메일 형식으로 입력해야 합니다.")));
	}

	/**
	 * 테스트를 위한 CustomerRegisterRequest 생성
	 */
	private CustomerRegisterRequest getCustomerRegisterRequest() {
		return CustomerRegisterRequest.builder()
			.name("nuri")
			.password("abc123")
			.phoneNumber("01082828282")
			.email("nuri@nhnacademy.com")
			.build();
	}

	/**
	 * 테스트를 위한 CustomerRegisterResponse 생성
	 */
	private CustomerRegisterResponse getCustomerRegisterResponse() {
		return CustomerRegisterResponse.builder()
			.name("nuri")
			.phoneNumber("01082828282")
			.email("nuri@nhnacademy.com")
			.build();
	}

	/**
	 * 테스트를 위한 잘못된 CustomerRegisterRequest 생성
	 */
	private CustomerRegisterRequest getBadCustomerRegisterRequest() {
		return CustomerRegisterRequest.builder()
			.name(null)
			.password("   ")
			.phoneNumber(null)
			.email("nuri")
			.build();
	}
}
