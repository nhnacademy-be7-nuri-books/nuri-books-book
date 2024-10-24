package shop.nuribooks.books.service.member;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.member.request.CustomerRegisterRequest;
import shop.nuribooks.books.dto.member.request.CustomerUpdateRequest;
import shop.nuribooks.books.dto.member.response.CustomerRegisterResponse;
import shop.nuribooks.books.dto.member.response.CustomerUpdateResponse;
import shop.nuribooks.books.entity.member.Customer;
import shop.nuribooks.books.exception.member.CustomerNotFoundException;
import shop.nuribooks.books.exception.member.EmailAlreadyExistsException;
import shop.nuribooks.books.repository.member.CustomerRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

	private final CustomerRepository customerRepository;

	/**
	 * 비회원 저장 <br>
	 * 고객 생성 후 customerRepository에 저장
	 * @throws EmailAlreadyExistsException 이미 존재하는 이메일입니다.
	 * @param request
	 * CustomerCreateRequest로 이름, 비밀번호, 전화번호, 이메일을 받는다. <br>
	 * 이메일로 비회원 조회 후 이미 존재하는 이메일이면 예외 던짐
	 * @return customer 등록 후 입력한 이름, 전화번호, 이메일을 그대로 CustomerCreateResponse에 담아서 반환
	 */
	@Transactional
	public CustomerRegisterResponse registerCustomer(CustomerRegisterRequest request) {
		if (customerRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException("이미 존재하는 이메일입니다.");
		}

		Customer customer = Customer.builder()
			.name(request.getName())
			.password(request.getPassword())
			.phoneNumber(request.getPhoneNumber())
			.email(request.getEmail())
			.build();

		Customer savedCustomer = customerRepository.save(customer);

		return CustomerRegisterResponse.builder()
			.name(savedCustomer.getName())
			.phoneNumber(savedCustomer.getPhoneNumber())
			.email(savedCustomer.getEmail())
			.build();
	}

	/**
	 * 비회원 정보 수정
	 * @throws CustomerNotFoundException 존재하지 않는 고객입니다.
	 * @param customerId 비회원 기본키
	 * @param request CustomerUpdateRequest에 이름과 전화번호를 담아서 요청
	 * @return 비회원 정보 수정에 성공하면 이름과 전화번호를 CustomerUpdateRequest에 담아서 반환
	 */
	@Transactional
	public CustomerUpdateResponse updateCustomer(Long customerId, CustomerUpdateRequest request) {
		Customer findCustomer = customerRepository.findById(customerId)
			.orElseThrow(() -> new CustomerNotFoundException("존재하지 않는 고객입니다."));

		findCustomer.changeCustomerInformation(request.getName(), request.getPhoneNumber());

		return CustomerUpdateResponse.builder()
			.name(findCustomer.getName())
			.phoneNumber(findCustomer.getPhoneNumber())
			.build();
	}
}
