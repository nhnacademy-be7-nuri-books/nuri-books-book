package shop.nuribooks.books.member.customer.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import shop.nuribooks.books.common.config.QuerydslConfiguration;
import shop.nuribooks.books.member.customer.entity.Customer;

@DataJpaTest
@Import(QuerydslConfiguration.class)
class CustomerRepositoryTest {

	@Autowired
	private CustomerRepository customerRepository;

	@BeforeEach
	void setUp() {
		Customer newCustomer = Customer.builder()
			.name("nuri")
			.password("abc123")
			.phoneNumber("01082828282")
			.email("nuri@nhnacademy.com")
			.build();

		customerRepository.save(newCustomer);
	}

	@DisplayName("이메일로 비회원 등록 여부 확인")
	@Test
	void existsByEmail() {
		//given
		String email = "nuri@nhnacademy.com";

		//when
		boolean exists = customerRepository.existsByEmail(email);

		//then
		assertThat(exists).isTrue();
	}

	@DisplayName("전화번호로 비회원 등록 여부 확인")
	@Test
	void existsByPhoneNumber() {
		//given
		String phoneNumber = "01082828282";

		//when
		boolean exists = customerRepository.existsByPhoneNumber(phoneNumber);

		//then
		assertThat(exists).isTrue();
	}

	@DisplayName("이메일로 비회원 조회")
	@Test
	void findByEmail() {
		//given
		String email = "nuri@nhnacademy.com";

		//when
		Optional<Customer> foundCustomer = customerRepository.findByEmail(email);

		//then
		assertThat(foundCustomer).isPresent();
	}
}
