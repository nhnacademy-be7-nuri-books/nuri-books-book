package shop.nuribooks.books.order.refund.service;

import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.order.refund.dto.request.RefundRequest;
import shop.nuribooks.books.order.refund.dto.response.RefundInfoResponse;
import shop.nuribooks.books.order.refund.dto.response.RefundResponse;

public interface RefundService {

	RefundInfoResponse getRefundInfoResponse(Long orderId, Pageable pageable);

	RefundResponse refund(Long orderId, RefundRequest refundRequest);
}
