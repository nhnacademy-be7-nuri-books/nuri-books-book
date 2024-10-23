package shop.nuribooks.books.controller.member;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.member.ResponseMessage;
import shop.nuribooks.books.dto.member.request.CustomerCreateRequest;
import shop.nuribooks.books.service.member.CustomerServiceImpl;

@RestController
@RequiredArgsConstructor
public class CustomerController {

	private final CustomerServiceImpl customerServiceImpl;

	// @PostMapping("api/member/customer")
	// public ResponseEntity<CustomerCreateRequest> customerCreate(
	// 	@RequestBody @Valid CustomerCreateRequest request, BindingResult bindingResult) {
	// 	if (bindingResult.hasErrors()) {
	// 		String errorMessage = bindingResult.getFieldErrors().getFirst().getDefaultMessage();
	//
	// 		return ResponseEntity.status(BAD_REQUEST).body(new ResponseMessage(BAD_REQUEST.value(), errorMessage));
	// 	}
	//
	// 	customerServiceImpl.createCustomer(request);
	// }

}
