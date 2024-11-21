package shop.nuribooks.books.order.refund.service;

import shop.nuribooks.books.order.refund.dto.request.RefundRequest;
import shop.nuribooks.books.order.refund.dto.response.RefundResponse;

public interface RefundService {
	RefundResponse refund(RefundRequest refundRequest);
}
