package shop.nuribooks.books.member.address.repository;

import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.*;
import static shop.nuribooks.books.member.member.entity.AuthorityEnum.MEMBER;
import static shop.nuribooks.books.member.member.entity.GradeEnum.STANDARD;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import shop.nuribooks.books.member.address.entity.Address;
import shop.nuribooks.books.member.member.entity.Customer;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.entity.StatusEnum;
import shop.nuribooks.books.member.member.repository.CustomerRepository;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@DataJpaTest
class AddressRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AddressRepository addressRepository;

    @DisplayName("유저의 주소를 가져온다.")
    @Test
    void findAllByMemberId() {
        // given
        Customer customer = createCustomer();
        customerRepository.save(customer);

        Member member = createMember(customer);
        memberRepository.save(member);

        Address address = createAddress(member);
        addressRepository.save(address);
        // when
        List<Address> allByMemberId = addressRepository.findAllByMemberId(member.getId());

        // then
        assertThat(allByMemberId).hasSize(1);
    }

    private Address createAddress(Member member) {
        return Address.builder()
                .member(member)
                .name("test")
                .address("장말로")
                .addressDetail("103호")
                .isDefault(true)
                .build();
    }

    private Member createMember(Customer customer) {
        return Member.builder()
                .customer(customer)
                .authority(MEMBER)
                .grade(STANDARD)
                .userId("nuriaaaaaa")
                .status(StatusEnum.ACTIVE)
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
}