package shop.nuribooks.books.order.refund.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.order.refund.dto.request.RefundRequest;
import shop.nuribooks.books.order.refund.dto.response.RefundResponse;
import shop.nuribooks.books.order.refund.service.RefundService;

@RequiredArgsConstructor
@RestController
public class RefundController {

	private final RefundService refundService;

	@PostMapping("/api/refunds")
	public ResponseEntity<RefundResponse> refundOrder(@RequestBody @Valid RefundRequest refundRequest) {
		RefundResponse response = refundService.refund(refundRequest);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
