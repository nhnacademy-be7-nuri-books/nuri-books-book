package shop.nuribooks.books.common.filter;

import static java.math.BigDecimal.ZERO;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shop.nuribooks.books.member.member.entity.AuthorityType.MEMBER;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import shop.nuribooks.books.member.address.dto.requset.AddressRegisterRequest;
import shop.nuribooks.books.member.address.service.AddressServiceImpl;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.customer.repository.CustomerRepository;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.grade.repository.GradeRepository;
import shop.nuribooks.books.member.member.entity.GenderType;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.entity.StatusType;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class ThreadLocalTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private AddressServiceImpl addressService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @DisplayName("Thread Local에 등록한다")
    @Test
    @Transactional
    void registerAddress() throws Exception {
        // given
        Grade creategrade = creategrade();
        Grade savedGrade = gradeRepository.saveAndFlush(creategrade);

        Customer customer = createCustomer();
        Customer savedCustomer = customerRepository.saveAndFlush(customer);

        Member member = createMember(savedCustomer, savedGrade);
        memberRepository.saveAndFlush(member);

        AddressRegisterRequest request = AddressRegisterRequest.builder()
                .name("test")
                .address("장말로")
                .addressDetail("103호")
                .isDefault(true)
                .build();

        // when
        mockMvc.perform(post("/api/member/address")
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-USER-ID", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated());
        // then
    }

    private Member createMember(Customer customer, Grade grade) {
        return Member.builder()
                .customer(customer)
                .authority(MEMBER)
                .grade(grade)
                .gender(GenderType.MALE)
                .username("nuriaaaaaa")
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
