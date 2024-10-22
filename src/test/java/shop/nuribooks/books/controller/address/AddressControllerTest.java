package shop.nuribooks.books.controller.address;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import shop.nuribooks.books.dto.address.requset.AddressCreateRequest;
import shop.nuribooks.books.dto.address.requset.AddressEditRequest;
import shop.nuribooks.books.entity.address.Address;
import shop.nuribooks.books.exception.address.AddressNotFoundException;
import shop.nuribooks.books.repository.address.AddressRepository;
import shop.nuribooks.books.service.address.AddressService;


@AutoConfigureMockMvc
@SpringBootTest
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AddressService addressService;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        addressRepository.deleteAllInBatch();
    }

    @DisplayName("회원의 주소를 등록한다.")
    @Test
    void addAddress() throws Exception {
        // given
        AddressCreateRequest request = AddressCreateRequest.builder()
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
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(status().isOk());
        // then
    }

    @DisplayName("주소 생성 시 올바르지 않은 필드 입력시 예외가 발생한다.")
    @Test
    void addAddressWithBadRequest() throws Exception {
        // given
        AddressCreateRequest request = AddressCreateRequest.builder()
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
        Address address1 = Address.builder()
                .memberId(1L)
                .name("test")
                .address("장말로")
                .addressDetail("103호")
                .isDefault(true)
                .build();

        Address address2 = Address.builder()
                .memberId(1L)
                .name("test")
                .address("장말로")
                .addressDetail("103호")
                .isDefault(true)
                .build();
        addressRepository.saveAll(List.of(address1, address2));

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
        Address address = Address.builder()
                .memberId(1L)
                .name("test")
                .address("장말로")
                .addressDetail("103호")
                .isDefault(true)
                .build();
        Address saved = addressRepository.save(address);

        // when
        mockMvc.perform(delete("/api/member/{memberId}/address/{addressId}", 1L, saved.getId()))
                .andExpect(status().isOk());
        // then
    }

    @DisplayName("회원의 등록된 주소를 수정한다.")
    @Test
    void addressModify() throws Exception{
        // given
        Address address = Address.builder()
                .memberId(1L)
                .name("test")
                .address("장말로")
                .addressDetail("103호")
                .isDefault(true)
                .build();

        Address saved = addressRepository.save(address);

        AddressEditRequest addressEditRequest = AddressEditRequest.builder()
                .id(saved.getId())
                .memberId(1L)
                .name("test")
                .address("장말로")
                .addressDetail("103호")
                .isDefault(false)
                .build();
        // when // then
        mockMvc.perform(patch("/api/member/{memberId}/address/{addressId}", 1L, saved.getId())
                        .content(objectMapper.writeValueAsString(addressEditRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.default").value(false))
                .andExpect(status().isOk());
    }
}