package shop.nuribooks.books.payment.payment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.payment.payment.dto.PaymentSuccessRequest;
import shop.nuribooks.books.payment.payment.service.PaymentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@Slf4j
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping
	public ResponseEntity<ResponseMessage> donePayment(@RequestBody PaymentSuccessRequest paymentSuccessRequest) {

		ResponseMessage result = paymentService.donePayment(paymentSuccessRequest);

		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}
}
