package shop.nuribooks.books.member.address.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.member.address.dto.requset.AddressEditRequest;
import shop.nuribooks.books.member.address.dto.requset.AddressRegisterRequest;
import shop.nuribooks.books.member.address.dto.response.AddressResponse;
import shop.nuribooks.books.member.address.service.AddressServiceImpl;

@WebMvcTest(AddressController.class)
class AddressControllerTest {

	@MockBean
	protected AddressServiceImpl addressService;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;

	@DisplayName("회원의 주소를 등록한다.")
	@Test
	void registerAddress() throws Exception {
		// given

		AddressRegisterRequest request = AddressRegisterRequest.builder()
			.name("test")
			.zipcode("12345")
			.address("장말로")
			.detailAddress("103호")
			.isDefault(true)
			.build();

		// when
		mockMvc.perform(post("/api/members/addresses")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isCreated());
		// then
	}

	@DisplayName("주소 생성 시 올바르지 않은 필드 입력시 예외가 발생한다.")
	@Test
	void registerAddressWithBadRequest() throws Exception {
		// given
		AddressRegisterRequest request = AddressRegisterRequest.builder()
			.name("")
			.zipcode("12345")
			.address("장말로")
			.detailAddress("103호")
			.isDefault(true)
			.build();

		// when
		mockMvc.perform(post("/api/members/addresses")
				.content(objectMapper.writeValueAsString(request))
				.header("X-USER-ID", "nuriaaaaaa")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isBadRequest());
		// then
	}

	@DisplayName("회원의 주소를 조회한다.")
	@Test
	void addressList() throws Exception {
		// given

		AddressResponse addressResponse1 = AddressResponse.builder()
			.name("name")
			.zipcode("1234")
			.address("address")
			.detailAddress("addressDetail")
			.isDefault(false)
			.build();

		AddressResponse addressResponse2 = AddressResponse.builder()
			.name("name")
			.zipcode("1234")
			.address("address")
			.detailAddress("addressDetail")
			.isDefault(false)
			.build();

		when(addressService.findAddressesByMemberId(any())).thenReturn(
			List.of(addressResponse1, addressResponse2));

		// when
		mockMvc.perform(get("/api/members/me/addresses"))
			.andExpect(jsonPath("$.*", Matchers.hasSize(2)))
			.andExpect(status().isOk());
		// then
	}

	@DisplayName("회원의 주소를 삭제한다.")
	@Test
	void addressRemove() throws Exception {
		// given

		// when
		mockMvc.perform(delete("/api/members/addresses/{addressId}", 1L, 1L))
			.andExpect(status().isOk());
		// then
	}

	@DisplayName("회원의 등록된 주소를 수정한다.")
	@Test
	void addressModify() throws Exception {
		// given

		AddressResponse addressResponse = AddressResponse.builder()
			.name("name")
			.zipcode("1234")
			.address("address")
			.detailAddress("addressDetail")
			.isDefault(false)
			.build();

		AddressEditRequest addressEditRequest = AddressEditRequest.builder()
			.name("test")
			.zipcode("1234")
			.address("장말로")
			.detailAddress("103호")
			.build();

		when(addressService.modifyAddress(1L, addressEditRequest)).thenReturn(addressResponse);

		// when // then
		mockMvc.perform(put("/api/members/addresses/{addressId}", 1L)
				.content(objectMapper.writeValueAsString(addressEditRequest))
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk());
	}

}
