package shop.nuribooks.books.member.customer.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.nuribooks.books.exception.member.AlreadyMemberException;
import shop.nuribooks.books.exception.member.CustomerNotFoundException;
import shop.nuribooks.books.exception.member.EmailAlreadyExistsException;
import shop.nuribooks.books.exception.member.PhoneNumberAlreadyExistsException;
import shop.nuribooks.books.member.customer.dto.request.CustomerRegisterRequest;
import shop.nuribooks.books.member.customer.dto.response.CustomerAuthInfoResponse;
import shop.nuribooks.books.member.customer.dto.response.CustomerRegisterResponse;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.customer.repository.CustomerRepository;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

	@InjectMocks
	private CustomerServiceImpl customerServiceImpl;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private MemberRepository memberRepository;

	@DisplayName("비회원 등록 성공")
	@Test
	void registerCustomer() {
		//given
		CustomerRegisterRequest request = getCustomerRegisterRequest();
		Customer savedCustomer = getSavedCustomer();

		when(customerRepository.existsByEmail(request.email())).thenReturn(false);
		when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

		//when
		CustomerRegisterResponse response = customerServiceImpl.registerCustomer(request);

		//then
		assertThat(response.name()).isEqualTo(request.name());
		assertThat(response.phoneNumber()).isEqualTo(request.phoneNumber());
		assertThat(response.email()).isEqualTo(request.email());
	}

	@DisplayName("비회원 등록 실패 - 중복된 이메일")
	@Test
	void registerCustomer_emailAlreadyExists() {
		//given
		CustomerRegisterRequest request = getCustomerRegisterRequest();

		when(customerRepository.existsByEmail(request.email())).thenReturn(true);

		//when / then
		assertThatThrownBy(() -> customerServiceImpl.registerCustomer(request))
			.isInstanceOf(EmailAlreadyExistsException.class)
			.hasMessage("이미 존재하는 이메일입니다.");
	}

	@DisplayName("비회원 등록 실패 - 중복된 전화번호")
	@Test
	void registerCustomer_phoneNumberAlreadyExists() {
		//given
		CustomerRegisterRequest request = getCustomerRegisterRequest();

		when(customerRepository.existsByEmail(request.email())).thenReturn(false);
		when(customerRepository.existsByPhoneNumber(request.phoneNumber())).thenReturn(true);

		//when / then
		assertThatThrownBy(() -> customerServiceImpl.registerCustomer(request))
			.isInstanceOf(PhoneNumberAlreadyExistsException.class)
			.hasMessage("이미 존재하는 전화번호입니다.");
	}

	@DisplayName("이메일로 비회원 인증 정보 조회 성공")
	@Test
	void getCustomerAuthInfoByEmail() {
		//given
		String email = "member50@naver.com";
		Customer savedCustomer = getSavedCustomer();

		when(customerRepository.findByEmail(email)).thenReturn(Optional.of(savedCustomer));
		when(memberRepository.existsById(savedCustomer.getId())).thenReturn(false);

		//when
		CustomerAuthInfoResponse response = customerServiceImpl.getCustomerAuthInfoByEmail(email);

		//then
		assertThat(response.customerId()).isEqualTo(savedCustomer.getId());
		assertThat(response.password()).isEqualTo(savedCustomer.getPassword());
		assertThat(response.email()).isEqualTo(savedCustomer.getEmail());
	}

	@DisplayName("이메일로 비회원 인증 정보 조회 실패 - 존재하지 않는 고객")
	@Test
	void getCustomerAuthInfoByEmail_customerNotFound() {
		//given
		String email = "member50@naver.com";

		when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());

		//when/then
		assertThatThrownBy(() -> customerServiceImpl.getCustomerAuthInfoByEmail(email))
			.isInstanceOf(CustomerNotFoundException.class)
			.hasMessage("존재하지 않는 고객입니다.");
	}

	@DisplayName("이메일로 비회원 인증 정보 조회 실패 - 이미 회원인 고객")
	@Test
	void getCustomerAuthInfoByEmail_alreadyMember() {
		//given
		String email = "member50@naver.com";
		Customer savedCustomer = getSavedCustomer();

		//when
		when(customerRepository.findByEmail(email)).thenReturn(Optional.of(savedCustomer));
		when(memberRepository.existsById(savedCustomer.getId())).thenReturn(true);

		//then
		assertThatThrownBy(() -> customerServiceImpl.getCustomerAuthInfoByEmail(email))
			.isInstanceOf(AlreadyMemberException.class)
			.hasMessage("비회원이 아닌 회원입니다.");
	}

	/**
	 * 테스트를 위한 CustomerRegisterRequest 생성
	 */
	private CustomerRegisterRequest getCustomerRegisterRequest() {
		return CustomerRegisterRequest.builder()
			.name("nuri")
			.password("abc123")
			.phoneNumber("042-8282-8282")
			.email("nuri@nhnacademy.com")
			.build();
	}

	/**
	 * 테스트를 위한 비회원 생성
	 */
	private Customer getSavedCustomer() {
		return Customer.builder()
			.id(1L)
			.name("nuri")
			.password("abc123")
			.phoneNumber("042-8282-8282")
			.email("nuri@nhnacademy.com")
			.build();
	}
}
