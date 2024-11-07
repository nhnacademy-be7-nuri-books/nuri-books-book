package shop.nuribooks.books.member.address.repository;

import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.*;
import static shop.nuribooks.books.member.member.entity.AuthorityType.MEMBER;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import shop.nuribooks.books.common.config.QuerydslConfiguration;
import shop.nuribooks.books.member.address.entity.Address;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.grade.repository.GradeRepository;
import shop.nuribooks.books.member.member.entity.GenderType;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.entity.StatusType;
import shop.nuribooks.books.member.customer.repository.CustomerRepository;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@DataJpaTest
@Import(QuerydslConfiguration.class)
class AddressRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @DisplayName("유저의 주소를 가져온다.")
    @Test
    void findAllByMember_UserId() {
        // given
        Grade grade = creategrade();
        gradeRepository.save(grade);

        Customer customer = createCustomer();
        customerRepository.save(customer);

        Member member = createMember(customer, grade);
        memberRepository.save(member);

        Address address = createAddress(member);
        addressRepository.save(address);
        // when
        List<Address> allByMemberId = addressRepository.findAllByMemberId(member.getId());

        // then
        assertThat(allByMemberId).hasSize(1);
    }

    private Address createAddress(Member member) {
        Address address = Address.builder()
                .name("test")
                .address("장말로")
                .addressDetail("103호")
                .isDefault(true)
                .build();
        address.setMember(member);
        return address;
    }

    private Member createMember(Customer customer, Grade grade) {
        return Member.builder()
                .customer(customer)
                .authority(MEMBER)
                .grade(grade)
                .username("nuriaaaaaa")
                .gender(GenderType.MALE)
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
                .name("name")
                .password("password")
                .phoneNumber("042-8282-8282")
                .email("nhnacademy@nuriBooks.com")
                .build();
    }

    /**
     * 테스트를 위한 Grade 생성
     */
    private Grade creategrade() {
        return Grade.builder()
            .name("STANDARD")
            .pointRate(3)
            .requirement(BigDecimal.valueOf(100_000))
            .build();
    }

}