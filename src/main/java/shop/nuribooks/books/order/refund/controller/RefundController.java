package shop.nuribooks.books.order.refund.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.order.refund.dto.request.RefundRequest;
import shop.nuribooks.books.order.refund.dto.response.RefundInfoResponse;
import shop.nuribooks.books.order.refund.dto.response.RefundResponse;
import shop.nuribooks.books.order.refund.service.RefundService;

@RequiredArgsConstructor
@RestController
public class RefundController {

	private final RefundService refundService;

	@GetMapping("/api/orders/refunds/{order-id}")
	public ResponseEntity<RefundInfoResponse> getRefundResponse(
		@PathVariable(name = "order-id") Long orderId) {
		RefundInfoResponse refundResponseInfo = refundService.getRefundInfoResponse(orderId);
		return ResponseEntity.ok().body(refundResponseInfo);
	}

	@PostMapping("/api/orders/refunds/{order-id}")
	public ResponseEntity<Void> registerRefund(@PathVariable(name = "order-id") Long orderId,
		@RequestBody RefundRequest refundRequest) {
		RefundResponse response = refundService.refund(orderId, refundRequest);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
