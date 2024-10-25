package shop.nuribooks.books.controller.address;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import shop.nuribooks.books.dto.address.requset.AddressRegisterRequest;
import shop.nuribooks.books.dto.address.requset.AddressEditRequest;
import shop.nuribooks.books.dto.address.response.AddressResponse;
import shop.nuribooks.books.service.address.AddressServiceImpl;


@WebMvcTest(controllers = AddressController.class)
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private AddressServiceImpl addressService;

    @DisplayName("회원의 주소를 등록한다.")
    @Test
    void registerAddress() throws Exception {
        // given
        AddressRegisterRequest request = AddressRegisterRequest.builder()
                .memberId(1L)
                .name("test")
                .address("장말로")
                .addressDetail("103호")
                .isDefault(true)
                .build();

        // when
        mockMvc.perform(post("/api/member/{memberId}/address", 1L)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        // then
    }

    @DisplayName("주소 생성 시 올바르지 않은 필드 입력시 예외가 발생한다.")
    @Test
    void registerAddressWithBadRequest() throws Exception {
        // given
        AddressRegisterRequest request = AddressRegisterRequest.builder()
                .memberId(null)
                .name("test")
                .address("장말로")
                .addressDetail("103호")
                .isDefault(true)
                .build();

        // when
        mockMvc.perform(post("/api/member/{memberId}/address", 1L)
                        .content(objectMapper.writeValueAsString(request))
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
                .id(1L)
                .name("name")
                .address("address")
                .addressDetail("addressDetail")
                .isDefault(false)
                .build();

        AddressResponse addressResponse2 = AddressResponse.builder()
                .id(1L)
                .name("name")
                .address("address")
                .addressDetail("addressDetail")
                .isDefault(false)
                .build();


        when(addressService.findAddressesByMemberId(1L)).thenReturn(List.of(addressResponse1, addressResponse2));

        // when
        mockMvc.perform(get("/api/member/{memberId}/address", 1L))
                .andExpect(jsonPath("$.*", Matchers.hasSize(2)))
                .andExpect(status().isOk());
        // then
    }

    @DisplayName("회원의 주소를 삭제한다.")
    @Test
    void addressRemove() throws Exception {
        // given

        // when
        mockMvc.perform(delete("/api/member/{memberId}/address/{addressId}", 1L, 1L))
                .andExpect(status().isOk());
        // then
    }

    @DisplayName("회원의 등록된 주소를 수정한다.")
    @Test
    void addressModify() throws Exception{
        // given

        AddressResponse addressResponse = AddressResponse.builder()
                .id(1L)
                .name("name")
                .address("address")
                .addressDetail("addressDetail")
                .isDefault(false)
                .build();

        AddressEditRequest addressEditRequest = AddressEditRequest.builder()
                .id(1L)
                .name("test")
                .address("장말로")
                .addressDetail("103호")
                .isDefault(false)
                .build();

        when(addressService.modifyAddress(addressEditRequest)).thenReturn(addressResponse);

        // when // then
        mockMvc.perform(patch("/api/member/{memberId}/address/{addressId}", 1L, 1L)
                        .content(objectMapper.writeValueAsString(addressEditRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

}