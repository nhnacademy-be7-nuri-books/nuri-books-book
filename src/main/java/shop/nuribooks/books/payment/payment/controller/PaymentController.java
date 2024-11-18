package shop.nuribooks.books.payment.payment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
	public ResponseEntity<ResponseMessage> DonePayment(PaymentSuccessRequest paymentSuccessRequest){

		ResponseMessage result = paymentService.DonePayment(paymentSuccessRequest);

		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
}
