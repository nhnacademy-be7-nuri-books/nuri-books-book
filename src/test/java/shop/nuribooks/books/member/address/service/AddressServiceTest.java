package shop.nuribooks.books.member.address.service;

import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.*;
import static shop.nuribooks.books.member.member.entity.AuthorityType.MEMBER;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import shop.nuribooks.books.member.address.dto.requset.AddressRegisterRequest;
import shop.nuribooks.books.member.address.dto.requset.AddressEditRequest;
import shop.nuribooks.books.member.address.dto.response.AddressResponse;
import shop.nuribooks.books.member.address.entity.Address;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.grade.repository.GradeRepository;
import shop.nuribooks.books.member.member.entity.GenderType;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.entity.StatusType;
import shop.nuribooks.books.exception.address.AddressNotFoundException;
import shop.nuribooks.books.member.address.repository.AddressRepository;
import shop.nuribooks.books.member.customer.repository.CustomerRepository;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@Transactional
@SpringBootTest
class AddressServiceTest {

    @Autowired
    private AddressServiceImpl addressService;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @DisplayName("회원의 주소를 생성한다.")
    @Test
    void addAddress() {
        // given
        Customer customer = createCustomer();
        customerRepository.save(customer);

        Grade grade = creategrade();
        gradeRepository.save(grade);

        Member member = createMember(customer, grade);
        memberRepository.save(member);

        AddressRegisterRequest request = AddressRegisterRequest.builder()
                .name("test")
                .address("장말로")
                .addressDetail("103호")
                .isDefault(true)
                .build();
        // when
        AddressResponse response = addressService.registerAddress(request, member.getId());

        // then
        assertThat(response.name()).isEqualTo("test");
    }

    @DisplayName("회원의 주소 리스트를 조회한다.")
    @Test
    void findAddressesByMemberId() {
        // given
        Customer customer = createCustomer();
        customerRepository.save(customer);

        Grade grade = creategrade();
        gradeRepository.save(grade);

        Member member = createMember(customer, grade);
        memberRepository.save(member);

        Address address = createAddress(member);
        addressRepository.save(address);

        // when
        List<AddressResponse> addressesByMemberId = addressService.findAddressesByMemberId(member.getId());

        // then
        assertThat(addressesByMemberId).hasSize(1);
    }

    @DisplayName("회원의 등록된 주소를 삭제한다.")
    @Test
    void removeAddress() {
        // given

        Customer customer = createCustomer();
        customerRepository.save(customer);

        Grade grade = creategrade();
        gradeRepository.save(grade);

        Member member = createMember(customer, grade);
        memberRepository.save(member);

        Address address = createAddress(member);
        addressRepository.save(address);

        Address saved = addressRepository.save(address);
        // when
        addressService.removeAddress(saved.getId());

        // then
        Assertions.assertThat(addressRepository.count()).isEqualTo(0);
    }

    @DisplayName("회원의 등록된 주소를 수정한다.")
    @Test
    void modifyAddress() {
        // given
        Customer customer = createCustomer();
        customerRepository.save(customer);

        Grade grade = creategrade();
        gradeRepository.save(grade);

        Member member = createMember(customer, grade);
        memberRepository.save(member);

        Address address = createAddress(member);
        addressRepository.save(address);

        Address saved = addressRepository.save(address);

        AddressEditRequest addressEditRequest = AddressEditRequest.builder()
                .name("test")
                .address("장말로")
                .addressDetail("103호")
                .isDefault(false)
                .build();
        // when
        addressService.modifyAddress(saved.getId(), addressEditRequest);

        // then
        Address changedAddress = addressRepository.findById(saved.getId())
                .orElseThrow(() -> new AddressNotFoundException("주소가 없습니다."));
        Assertions.assertThat(changedAddress.isDefault()).isFalse();
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