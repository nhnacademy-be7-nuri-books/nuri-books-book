package shop.nuribooks.books.common.filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.member.address.dto.requset.AddressRegisterRequest;
import shop.nuribooks.books.member.address.service.AddressServiceImpl;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.customer.repository.CustomerRepository;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.grade.repository.GradeRepository;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@SpringBootTest
@AutoConfigureMockMvc
class ThreadLocalTest {
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private AddressServiceImpl addressService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private GradeRepository gradeRepository;

	@DisplayName("Thread Local에 등록한다")
	@Test
	@Transactional
	void registerAddress() throws Exception {
		// given
		Grade creategrade = TestUtils.creategrade();
		Grade savedGrade = gradeRepository.save(creategrade);

		Customer customer = TestUtils.createCustomer();
		Customer savedCustomer = customerRepository.save(customer);

		Member member = TestUtils.createMember(savedCustomer, savedGrade);
		Member saved = memberRepository.save(member);

		AddressRegisterRequest request = AddressRegisterRequest.builder()
			.name("test")
			.zipcode("12345")
			.address("장말로")
			.detailAddress("103호")
			.isDefault(true)
			.build();

		// when
		mockMvc.perform(post("/api/members/addresses")
				.content(objectMapper.writeValueAsString(request))
				.header("X-USER-ID", saved.getId())
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isCreated());
		// then
	}

}
