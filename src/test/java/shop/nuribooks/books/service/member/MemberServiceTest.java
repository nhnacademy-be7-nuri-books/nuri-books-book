package shop.nuribooks.books.service.member;

import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import shop.nuribooks.books.dto.member.request.MemberCreateReq;
import shop.nuribooks.books.entity.member.Customer;
import shop.nuribooks.books.repository.member.CustomerRepository;
import shop.nuribooks.books.repository.member.MemberRepository;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@InjectMocks
	private MemberService memberService;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private MemberRepository memberRepository;

	@Spy
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@DisplayName("회원가입")
	@Test
	void memberCreate() {
		// given
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		MemberCreateReq request = memberCreateRequest();
		String encryptedPassword = encoder.encode(request.getPassword());

		doReturn(new Customer(1L, request.getName(), encryptedPassword, request.getPhoneNumber(), request.getEmail()))
			.when(customerRepository)
			.save(any(Customer.class));

		// doReturn(new Member(customer, MEMBER, STANDARD, ))
		// 	.when(memberRepository)
		// 	.save(any(Member.class));

		// when

		// then
	}

	private MemberCreateReq memberCreateRequest() {
		return MemberCreateReq.builder()
			.name("boho")
			.userId("nuri95")
			.password("abc123")
			.phoneNumber("042-8282-8282")
			.email("nhnacademy@nuriBooks.com")
			.birthday(LocalDate.of(1988, 8, 12))
			.build();
	}
}