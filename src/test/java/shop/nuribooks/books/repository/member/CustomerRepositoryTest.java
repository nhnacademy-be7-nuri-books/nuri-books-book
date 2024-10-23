package shop.nuribooks.books.repository.member;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import shop.nuribooks.books.entity.member.Customer;

@DataJpaTest
class CustomerRepositoryTest {

	@Autowired
	private CustomerRepository customerRepository;

	@DisplayName("입력된 이메일로 고객 존재 여부 확인")
	@Test
	void existsByEmail() {
		//given
		Customer customer = customer();
		Customer savedCustomer = customerRepository.save(customer);

		//when
		boolean exists = customerRepository.existsByEmail(savedCustomer.getEmail());

		//then
		assertThat(exists).isTrue();
	}

	@DisplayName("고객 아이디와 비밀번호로 존재 여부 확인")
	@Test
	public void existsByIdAndPassword() {
	    //given
		Customer customer = customer();
		Customer savedCustomer = customerRepository.save(customer);

	    //when
		boolean exists = customerRepository.existsByIdAndPassword(savedCustomer.getId(), savedCustomer.getPassword());

		//then
		assertThat(exists).isTrue();
	}

	private Customer customer() {
		return Customer.builder()
			.id(null)
			.name("nuri")
			.password("abc123")
			.phoneNumber("042-8282-8282")
			.email("nhnacademy@nuriBooks.com")
			.build();
	}

}