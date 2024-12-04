package shop.nuribooks.books.member.address.service;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.exception.address.AddressNotFoundException;
import shop.nuribooks.books.member.address.dto.requset.AddressEditRequest;
import shop.nuribooks.books.member.address.dto.requset.AddressRegisterRequest;
import shop.nuribooks.books.member.address.dto.response.AddressResponse;
import shop.nuribooks.books.member.address.entity.Address;
import shop.nuribooks.books.member.address.repository.AddressRepository;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.customer.repository.CustomerRepository;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.grade.repository.GradeRepository;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;

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

	private Member member;

	@BeforeEach
	void setUp() {
		Customer customer = TestUtils.createCustomer();
		Customer savedCustomer = customerRepository.save(customer);

		Grade grade = creategrade();
		Grade savedGrade = gradeRepository.save(grade);

		member = TestUtils.createMember(savedCustomer, savedGrade);
		memberRepository.save(member);
	}

	@DisplayName("회원의 주소를 생성한다.")
	@Test
	@Transactional
	void addAddress() {
		// given

		AddressRegisterRequest request = AddressRegisterRequest.builder()
			.name("test")
			.address("장말로")
			.detailAddress("103호")
			.isDefault(true)
			.build();
		// when
		AddressResponse response = addressService.registerAddress(request, member.getId());

		// then
		assertThat(response.name()).isEqualTo("test");
	}

	@DisplayName("회원의 주소 리스트를 조회한다.")
	@Test
	@Transactional
	void findAddressesByMemberId() {
		// given

		Address address = createAddress(member);
		addressRepository.save(address);
		member.addAddress(address);

		// when
		List<AddressResponse> addressesByMemberId = addressService.findAddressesByMemberId(member.getId());

		// then
		assertThat(addressesByMemberId).hasSize(1);
	}

	@DisplayName("회원의 등록된 주소를 삭제한다.")
	@Test
	@Transactional
	void removeAddress() {
		// given

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
	@Transactional
	void modifyAddress() {
		// given

		Address address = createAddress(member);
		addressRepository.save(address);

		Address saved = addressRepository.save(address);

		AddressEditRequest addressEditRequest = AddressEditRequest.builder()
			.name("test")
			.address("장말로")
			.detailAddress("103호")
			.build();
		// when
		addressService.modifyAddress(saved.getId(), addressEditRequest);

		// then
		Address changedAddress = addressRepository.findById(saved.getId())
			.orElseThrow(() -> new AddressNotFoundException("주소가 없습니다."));
		Assertions.assertThat(changedAddress.isDefault()).isTrue();
	}

	private Address createAddress(Member member) {
		Address address = Address.builder()
			.name("test")
			.address("장말로")
			.detailAddress("103호")
			.isDefault(true)
			.build();
		address.setMember(member);
		return address;
	}

	private Grade creategrade() {
		return Grade.builder()
			.name("STANDARD")
			.pointRate(3)
			.requirement(BigDecimal.valueOf(100_000))
			.build();
	}

}
