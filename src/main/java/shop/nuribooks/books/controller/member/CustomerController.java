package shop.nuribooks.books.controller.member;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.service.member.CustomerServiceImpl;

@RestController
@RequiredArgsConstructor
public class CustomerController {

	private final CustomerServiceImpl customerServiceImpl;

	// @PostMapping("api/member/customer")
	// public ResponseEntity<CustomerRegisterRequest> customerCreate(
	// 	@RequestBody @Valid CustomerRegisterRequest request, BindingResult bindingResult) {
	// 	if (bindingResult.hasErrors()) {
	// 		String errorMessage = bindingResult.getFieldErrors().getFirst().getDefaultMessage();
	//
	// 		return ResponseEntity.status(BAD_REQUEST).body(new ResponseMessage(BAD_REQUEST.value(), errorMessage));
	// 	}
	//
	// 	customerServiceImpl.createCustomer(request);
	// }

	// git 작동 확인 목적 수정

}
