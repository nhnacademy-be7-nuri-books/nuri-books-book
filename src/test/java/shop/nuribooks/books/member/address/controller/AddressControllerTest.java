package shop.nuribooks.books.member.address.controller;

import static java.math.BigDecimal.ZERO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shop.nuribooks.books.member.member.entity.AuthorityType.MEMBER;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import shop.nuribooks.books.member.address.dto.requset.AddressEditRequest;
import shop.nuribooks.books.member.address.dto.requset.AddressRegisterRequest;
import shop.nuribooks.books.member.address.dto.response.AddressResponse;
import shop.nuribooks.books.member.address.service.AddressServiceImpl;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.member.entity.GenderType;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.entity.StatusType;
import shop.nuribooks.books.member.member.repository.MemberRepository;


@WebMvcTest(controllers = AddressController.class)
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private AddressServiceImpl addressService;

    @MockBean
    private MemberRepository memberRepository;

    @DisplayName("회원의 주소를 등록한다.")
    @Test
    void registerAddress() throws Exception {
        // given
        Member member = createMember(createCustomer(), creategrade());
        when(memberRepository.findByUserId(any())).thenReturn(Optional.of(member));

        AddressRegisterRequest request = AddressRegisterRequest.builder()
                .name("test")
                .address("장말로")
                .addressDetail("103호")
                .isDefault(true)
                .build();

        // when
        mockMvc.perform(post("/api/member/address")
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
                .address("장말로")
                .addressDetail("103호")
                .isDefault(true)
                .build();

        // when
        mockMvc.perform(post("/api/member/address")
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
                .address("address")
                .addressDetail("addressDetail")
                .isDefault(false)
                .build();

        AddressResponse addressResponse2 = AddressResponse.builder()
                .name("name")
                .address("address")
                .addressDetail("addressDetail")
                .isDefault(false)
                .build();

        when(addressService.findAddressesByMemberId(any())).thenReturn(
                List.of(addressResponse1, addressResponse2));

        // when
        mockMvc.perform(get("/api/member/me/address"))
                .andExpect(jsonPath("$.*", Matchers.hasSize(2)))
                .andExpect(status().isOk());
        // then
    }

    @DisplayName("회원의 주소를 삭제한다.")
    @Test
    void addressRemove() throws Exception {
        // given

        // when
        mockMvc.perform(delete("/api/member/address/{addressId}", 1L, 1L))
                .andExpect(status().isOk());
        // then
    }

    @DisplayName("회원의 등록된 주소를 수정한다.")
    @Test
    void addressModify() throws Exception {
        // given

        AddressResponse addressResponse = AddressResponse.builder()
                .name("name")
                .address("address")
                .addressDetail("addressDetail")
                .isDefault(false)
                .build();

        AddressEditRequest addressEditRequest = AddressEditRequest.builder()
                .name("test")
                .address("장말로")
                .addressDetail("103호")
                .isDefault(false)
                .build();

        when(addressService.modifyAddress(1L, addressEditRequest)).thenReturn(addressResponse);

        // when // then
        mockMvc.perform(patch("/api/member/address/{addressId}", 1L)
                        .content(objectMapper.writeValueAsString(addressEditRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    private Member createMember(Customer customer, Grade grade) {
        return Member.builder()
                .customer(customer)
                .authority(MEMBER)
                .grade(grade)
                .gender(GenderType.MALE)
                .userId("nuriaaaaaa")
                .status(StatusType.ACTIVE)
                .birthday(LocalDate.of(1988, 8, 12))
                .createdAt(LocalDateTime.now())
                .point(ZERO)
                .totalPaymentAmount(ZERO)
                .latestLoginAt(null)
                .withdrawnAt(null)
                .build();
    }

    private Customer createCustomer() {
        return Customer.builder()
                .id(1L)
                .name("name")
                .password("password")
                .phoneNumber("042-8282-8282")
                .email("nhnacademy@nuriBooks.com")
                .build();
    }

    private Grade creategrade() {
        return Grade.builder()
                .name("STANDARD")
                .pointRate(3)
                .requirement(BigDecimal.valueOf(100_000))
                .build();
    }

}