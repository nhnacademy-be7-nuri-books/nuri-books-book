package shop.nuribooks.books.order.returnrequest.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.order.returnrequest.dto.request.ReturnRequestRegisterRequest;
import shop.nuribooks.books.order.returnrequest.dto.response.ReturnRequestResponse;
import shop.nuribooks.books.order.returnrequest.service.ReturnRequestService;

@RequiredArgsConstructor
@RestController
public class ReturnRequestController {
	private final ReturnRequestService returnRequestService;

	// 관리자가 반품요청들을 확인
	@GetMapping("/api/orders/return-requests")
	public ResponseEntity<Page<ReturnRequestResponse>> getWrappingPapers(Pageable pageable) {
		Page<ReturnRequestResponse> returnRequestResponse = returnRequestService.getReturnRequest(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(returnRequestResponse);
	}

	@PostMapping("/api/orders/return-requests/{order-id}")
	public ResponseEntity<ResponseMessage> registerWrappingPaper(@PathVariable(name = "order-id") Long orderId,
		@Valid @RequestBody ReturnRequestRegisterRequest returnRequestRegisterRequest
	) {
		returnRequestService.registerReturnRequest(orderId, returnRequestRegisterRequest);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new ResponseMessage(HttpStatus.CREATED.value(), "반품 요청 등록 성공"));
	}

}

