package shop.nuribooks.books.controller.address;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import shop.nuribooks.books.service.address.AddressService;


@AutoConfigureMockMvc
@SpringBootTest
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressService addressService;

    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("회원의 주소를 등록한다.")
    @Test
    void createAddress() throws Exception {
        // given
        AddressCreateRequest request = AddressCreateRequest.builder()
                .memberId(1L)
                .name("test")
                .address("장말로")
                .address("103호")
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
}