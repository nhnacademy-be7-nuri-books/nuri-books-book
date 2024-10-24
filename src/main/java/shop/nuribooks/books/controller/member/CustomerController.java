package shop.nuribooks.books.controller.member;

import static org.springframework.http.HttpStatus.*;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.member.request.CustomerRegisterRequest;
import shop.nuribooks.books.dto.member.request.CustomerUpdateRequest;
import shop.nuribooks.books.dto.member.response.CustomerRegisterResponse;
import shop.nuribooks.books.dto.member.response.CustomerUpdateResponse;
import shop.nuribooks.books.service.member.CustomerService;

@RestController
@RequiredArgsConstructor
public class CustomerController {

	private final CustomerService customerService;

	/**
	 * 비회원 등록
	 * @param request
	 * CustomerRegisterRequest에 비회원 이름, 비밀번호, 전화번호, 이메일을 담아서 요청
	 * @return 비회원 등록에 성공하면 이름, 전화번호, 이메일을 CustomerRegisterResponse에 담아서 반환
	 */
	@PostMapping("/api/member/customer")
	public ResponseEntity<CustomerRegisterResponse> customerRegister(
		@RequestBody @Valid CustomerRegisterRequest request) {

		CustomerRegisterResponse response = customerService.registerCustomer(request);

		return ResponseEntity.status(CREATED).body(response);
	}

	/**
	 * 비회원 정보 수정
	 * @param customerId 비회원 기본키
	 * @param request CustomerUpdateRequest에 이름과 전화번호를 담아서 요청
	 * @return 비회원 정보 수정에 성공하면 이름과 전화번호를 CustomerUpdateRequest에 담아서 반환
	 */
	@Deprecated
	@PostMapping("/api/member/customer/{customerId}")
	public ResponseEntity<CustomerUpdateResponse> customerUpdate(
		@PathVariable Long customerId, @RequestBody @Valid CustomerUpdateRequest request) {

		CustomerUpdateResponse response = customerService.updateCustomer(customerId, request);

		return ResponseEntity.status(OK).body(response);
	}
}




