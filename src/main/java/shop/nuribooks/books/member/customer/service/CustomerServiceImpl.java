package shop.nuribooks.books.member.customer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.exception.member.EmailAlreadyExistsException;
import shop.nuribooks.books.exception.member.PhoneNumberAlreadyExistsException;
import shop.nuribooks.books.member.customer.dto.DtoMapper;
import shop.nuribooks.books.member.customer.dto.EntityMapper;
import shop.nuribooks.books.member.customer.dto.request.CustomerRegisterRequest;
import shop.nuribooks.books.member.customer.dto.response.CustomerRegisterResponse;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.customer.repository.CustomerRepository;

/**
 * @author Jprotection
 */
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
	@Override
	@Transactional
	public CustomerRegisterResponse registerCustomer(CustomerRegisterRequest request) {
		if (customerRepository.existsByEmail(request.email())) {
			throw new EmailAlreadyExistsException("이미 존재하는 이메일입니다.");
		}
		if (customerRepository.existsByPhoneNumber(request.phoneNumber())) {
			throw new PhoneNumberAlreadyExistsException("이미 존재하는 전화번호입니다.");
		}

		Customer customer = EntityMapper.toCustomerEntity(request);

		Customer savedCustomer = customerRepository.save(customer);

		return DtoMapper.toRegisterDto(savedCustomer);
	}
}

