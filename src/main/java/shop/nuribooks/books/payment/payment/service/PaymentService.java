package shop.nuribooks.books.payment.payment.service;

import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.payment.payment.dto.PaymentSuccessRequest;

/**
 * 결제 서비스 인터페이스
 *
 * @author nuri
 */
public interface PaymentService {

	/**
	 * 결제 완료 처리
	 *
	 * @param paymentSuccessRequest 결제 테이블 저장용
	 * @return 성공/실패 메시지
	 */
	ResponseMessage donePayment(PaymentSuccessRequest paymentSuccessRequest);

	ResponseMessage cancelPayment(Order order, String reason);
}
