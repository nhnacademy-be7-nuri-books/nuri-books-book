package shop.nuribooks.books.member.customer.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.nuribooks.books.exception.member.EmailAlreadyExistsException;
import shop.nuribooks.books.exception.member.PhoneNumberAlreadyExistsException;
import shop.nuribooks.books.member.customer.dto.request.CustomerRegisterRequest;
import shop.nuribooks.books.member.customer.dto.response.CustomerRegisterResponse;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.customer.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

	@InjectMocks
	private CustomerServiceImpl customerServiceImpl;

	@Mock
	private CustomerRepository customerRepository;

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
