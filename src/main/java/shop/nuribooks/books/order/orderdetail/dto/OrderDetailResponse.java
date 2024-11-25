package shop.nuribooks.books.order.orderdetail.dto;

import org.springframework.data.domain.Page;

import lombok.Builder;
import shop.nuribooks.books.order.order.dto.OrderSummaryDto;
import shop.nuribooks.books.order.shipping.dto.ShippingInfoDto;
import shop.nuribooks.books.payment.payment.dto.PaymentInfoDto;

@Builder
public record OrderDetailResponse(
	OrderSummaryDto order,          // 주문의 요약 정보
	Page<OrderDetailItemDto> orderItems, // 주문 항목 리스트
	ShippingInfoDto shipping,      // 배송 정보
	PaymentInfoDto payment         // 결제 정보 (총 결제 금액, 배송비, 쿠폰비용, 포인트, 적립금)
) {
}