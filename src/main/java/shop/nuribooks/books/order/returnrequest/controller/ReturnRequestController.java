package shop.nuribooks.books.order.returnrequest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.order.returnrequest.dto.request.ReturnRequestRegisterRequest;
import shop.nuribooks.books.order.returnrequest.service.ReturnRequestService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/return-requests")
public class ReturnRequestController {
	private final ReturnRequestService returnRequestService;

	@PostMapping("/{order-id}")
	public ResponseEntity<ResponseMessage> registerWrappingPaper(@PathVariable(name = "order-id") Long orderId,
		@Valid @RequestBody ReturnRequestRegisterRequest returnRequestRegisterRequest
	) {
		returnRequestService.registerReturnRequest(orderId, returnRequestRegisterRequest);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new ResponseMessage(HttpStatus.CREATED.value(), "포장지 등록 성공"));
	}

}

