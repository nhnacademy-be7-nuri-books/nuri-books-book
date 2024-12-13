package shop.nuribooks.books.book.coupon.listener;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.book.coupon.message.BookCouponIssueMessage;
import shop.nuribooks.books.book.coupon.service.MemberCouponService;
import shop.nuribooks.books.common.config.rabbitmq.RabbitmqConfig;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponIssueListener {

	private final MemberCouponService memberCouponService;

	@Transactional
	@RabbitListener(queues = RabbitmqConfig.BOOK_COUPON_ISSUE_QUEUE, concurrency = "1")
	public void handleBookCouponIssue(BookCouponIssueMessage message) {
		log.info("Received inventory update message: {}", message);
		try {
			memberCouponService.issueBookCoupon(message);
			log.info("Successfully processed book coupon issue message.");
		} catch (AmqpRejectAndDontRequeueException ex) {
			log.error("Failed to process coupon issue: {}", ex.getMessage());
			throw ex;
		}
	}
}
