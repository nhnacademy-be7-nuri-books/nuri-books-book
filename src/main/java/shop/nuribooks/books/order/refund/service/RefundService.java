package shop.nuribooks.books.order.refund.service;

import shop.nuribooks.books.order.refund.dto.request.RefundRequest;
import shop.nuribooks.books.order.refund.dto.response.RefundInfoResponse;
import shop.nuribooks.books.order.refund.dto.response.RefundResponse;

public interface RefundService {

	RefundInfoResponse getRefundResponseInfo(Long orderDetailId);

	RefundResponse refund(RefundRequest refundRequest);
}