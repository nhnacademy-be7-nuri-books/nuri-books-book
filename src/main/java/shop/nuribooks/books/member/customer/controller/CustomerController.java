package shop.nuribooks.books.member.customer.controller;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.member.customer.dto.request.CustomerRegisterRequest;
import shop.nuribooks.books.member.customer.dto.response.CustomerAuthInfoResponse;
import shop.nuribooks.books.member.customer.dto.response.CustomerRegisterResponse;
import shop.nuribooks.books.member.customer.service.CustomerService;

/**
 * @author Jprotection
 */
@RestController
@RequestMapping("/api/members/customers")
@RequiredArgsConstructor
public class CustomerController {

	private final CustomerService customerService;

	/**
	 * 비회원 등록
	 * @param request
	 * CustomerRegisterRequest에 비회원 이름, 비밀번호, 전화번호, 이메일을 담아서 요청
	 * @return 비회원 등록에 성공하면 이름, 전화번호, 이메일을 CustomerRegisterResponse에 담아서 반환
	 */
	@Operation(summary = "신규 비회원 등록", description = "신규 비회원을 등록합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "신규 비회원 등록 성공"),
		@ApiResponse(responseCode = "400", description = "신규 비회원 등록 요청 데이터가 유효하지 않음"),
		@ApiResponse(responseCode = "409", description = "등록된 비회원이 이미 존재함")
	})
	@PostMapping
	public ResponseEntity<CustomerRegisterResponse> customerRegister(
		@RequestBody @Valid CustomerRegisterRequest request) {

		CustomerRegisterResponse response = customerService.registerCustomer(request);

		return ResponseEntity.status(CREATED).body(response);
	}

	/**
	 * 이메일로 비회원의 customerId, password, email을 조회
	 */
	@Operation(summary = "이메일로 비회원 인증 조회", description = "이메일로 비회원의 customerId, 비밀번호, 이메일을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "비회원 인증 조회 성공"),
		@ApiResponse(responseCode = "404", description = "비회원이 존재하지 않음")
	})
	@GetMapping("/{email}")
	public ResponseEntity<CustomerAuthInfoResponse> getCustomerAuthInfoByEmail(@PathVariable String email) {

		CustomerAuthInfoResponse response = customerService.getCustomerAuthInfoByEmail(email);

		return ResponseEntity.status(OK).body(response);
	}
}
