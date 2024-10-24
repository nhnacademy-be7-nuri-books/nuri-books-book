package shop.nuribooks.books.service.address;

import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.*;
import static shop.nuribooks.books.entity.member.AuthorityEnum.MEMBER;
import static shop.nuribooks.books.entity.member.GradeEnum.STANDARD;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import shop.nuribooks.books.dto.address.requset.AddressRegisterRequest;
import shop.nuribooks.books.dto.address.requset.AddressEditRequest;
import shop.nuribooks.books.dto.address.response.AddressResponse;
import shop.nuribooks.books.entity.address.Address;
import shop.nuribooks.books.entity.member.Customer;
import shop.nuribooks.books.entity.member.Member;
import shop.nuribooks.books.entity.member.StatusEnum;
import shop.nuribooks.books.exception.address.AddressNotFoundException;
import shop.nuribooks.books.repository.address.AddressRepository;
import shop.nuribooks.books.repository.member.CustomerRepository;
import shop.nuribooks.books.repository.member.MemberRepository;

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

    @DisplayName("회원의 주소를 생성한다.")
    @Test
    void addAddress() {
        // given
        Customer customer = createCustomer();
        customerRepository.save(customer);

        Member member = createMember(customer);
        memberRepository.save(member);

        AddressRegisterRequest request = AddressRegisterRequest.builder()
                .memberId(member.getId())
                .name("test")
                .address("장말로")
                .addressDetail("103호")
                .isDefault(true)
                .build();
        // when
        AddressResponse response = addressService.registerAddress(request);

        // then
        assertThat(response.getName()).isEqualTo("test");
    }

    @DisplayName("회원의 주소 리스트를 조회한다.")
    @Test
    void findAddressesByMemberId() {
        // given
        Customer customer = createCustomer();
        customerRepository.save(customer);

        Member member = createMember(customer);
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

        Member member = createMember(customer);
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

        Member member = createMember(customer);
        memberRepository.save(member);

        Address address = createAddress(member);
        addressRepository.save(address);

        Address saved = addressRepository.save(address);

        AddressEditRequest addressEditRequest = AddressEditRequest.builder()
                .id(saved.getId())
                .name("test")
                .address("장말로")
                .addressDetail("103호")
                .isDefault(false)
                .build();
        // when
        addressService.modifyAddress(addressEditRequest);

        // then
        Address changedAddress = addressRepository.findById(saved.getId())
                .orElseThrow(() -> new AddressNotFoundException("주소가 없습니다."));
        Assertions.assertThat(changedAddress.isDefault()).isFalse();
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