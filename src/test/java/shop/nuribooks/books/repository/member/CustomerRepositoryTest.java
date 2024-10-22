package shop.nuribooks.books.repository.member;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import shop.nuribooks.books.entity.member.Customer;

@DataJpaTest
class CustomerRepositoryTest {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@DisplayName("입력된 이메일로 고객 존재 여부 확인")
	@Test
	void existsByEmail() {
		//given
		Customer customer = customer();
		Customer savedCustomer = customerRepository.save(customer);

		//when
		boolean exists = customerRepository.existsByEmail(savedCustomer.getEmail());

		//then
		Assertions.assertThat(exists).isTrue();
	}

	private Customer customer() {
		return Customer.builder()
			.id(null)
			.name("nuri")
			.password(bCryptPasswordEncoder.encode("abc123"))
			.phoneNumber("042-8282-8282")
			.email("nhnacademy@nuriBooks.com")
			.build();
	}

}