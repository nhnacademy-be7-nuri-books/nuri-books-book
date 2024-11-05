package shop.nuribooks.books.member.customer.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import shop.nuribooks.books.common.config.QuerydslConfiguration;
import shop.nuribooks.books.member.customer.entity.Customer;

@DataJpaTest
public class CustomerRepositoryTest {

	@Autowired
	private CustomerRepository customerRepository;

	@DisplayName("이메일로 비회원 등록 여부 확인")
	@Test
	void existsByEmail() {
	    //given
		Customer savedCustomer = getSavedCustomer();
		String requiredEmail = "nuri@nhnacademy.com";

		//when
		boolean exists = customerRepository.existsByEmail(requiredEmail);

		//then
		assertThat(exists).isTrue();
	}

	@DisplayName("비회원 아이디(PK)와 비밀번호로 등록 여부 확인")
	@Test
	void existsByIdAndPassword() {
		//given
		Customer savedCustomer = getSavedCustomer();

		//when
		boolean exists = customerRepository.existsByIdAndPassword(savedCustomer.getId(), savedCustomer.getPassword());

		//then
		assertThat(exists).isTrue();
	}

	@DisplayName("비회원 아이디(PK)와 비밀번호로 해당 고객 반환")
	@Test
	void findByIdAndPassword() {
		//given
		Customer savedCustomer = getSavedCustomer();

		//when
		Optional<Customer> foundCustomer = customerRepository.findByIdAndPassword(
			savedCustomer.getId(), savedCustomer.getPassword());

		//then
		assertThat(foundCustomer).isPresent();
		assertThat(foundCustomer.get().getId()).isEqualTo(savedCustomer.getId());
		assertThat(foundCustomer.get().getPassword()).isEqualTo(savedCustomer.getPassword());
	}

	/**
	 * 테스트를 위해 repository에 비회원 저장 후 반환
	 */
	private Customer getSavedCustomer() {
		Customer newCustomer = Customer.builder()
			.name("nuri")
			.password("abc123")
			.phoneNumber("042-8282-8282")
			.email("nuri@nhnacademy.com")
			.build();

		return customerRepository.save(newCustomer);
	}
}
