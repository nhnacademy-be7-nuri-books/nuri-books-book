package shop.nuribooks.books.book.coupon.listener;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.entity.MemberCoupon;
import shop.nuribooks.books.book.coupon.enums.CouponType;
import shop.nuribooks.books.book.coupon.message.BookCouponIssueMessage;
import shop.nuribooks.books.book.coupon.repository.CouponRepository;
import shop.nuribooks.books.book.coupon.repository.MemberCouponRepository;
import shop.nuribooks.books.common.config.rabbitmq.RabbitmqConfig;
import shop.nuribooks.books.common.entity.ProcessedMessage;
import shop.nuribooks.books.common.repository.ProcessedMessageRepository;
import shop.nuribooks.books.exception.coupon.CouponNotFoundException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponIssueListener {

	private final ProcessedMessageRepository processedMessageRepository;
	private final CouponRepository couponRepository;
	private final MemberRepository memberRepository;
	private final MemberCouponRepository memberCouponRepository;

	@Transactional
	@RabbitListener(queues = RabbitmqConfig.BOOK_COUPON_ISSUE_QUEUE, concurrency = "1")
	public void handleBookCouponIssue(BookCouponIssueMessage message) {
		log.info("Received inventory update message: {}", message);

		// idempotency 체크
		if (processedMessageRepository.existsById(message.getMessageId())) {
			log.warn("Received a message with id {} which is already processed", message.getMessageId());
			throw new AmqpRejectAndDontRequeueException("AlreadyExists message");
		}

		Coupon coupon = couponRepository.findById(message.getCouponId())
			.orElseThrow(CouponNotFoundException::new);

		if (!coupon.getCouponType().equals(CouponType.BOOK)) {
			throw new AmqpRejectAndDontRequeueException("Invalid Book Coupon");
		}

		if (coupon.getQuantity() == null || coupon.getQuantity() <= 0) {
			log.error("Received a message with id {} which is invalid Stock", message.getMessageId());
			throw new AmqpRejectAndDontRequeueException("Invalid Stock");
		}

		Member member = memberRepository.findById(message.getMemberId())
			.orElseThrow(() -> new MemberNotFoundException("존재하지않는 회원입니다."));

		boolean alreadyIssued = memberCouponRepository.existsByMemberAndCoupon(member, coupon);
		if (alreadyIssued) {
			log.warn("Member {} already has coupon {}", member.getId(), coupon.getId());
			throw new AmqpRejectAndDontRequeueException("Member already has coupon");
		}

		//수량차감 메서드 호출

		MemberCoupon memberCoupon = MemberCoupon.builder()
			.coupon(coupon)
			.member(member)
			.build();
		memberCouponRepository.save(memberCoupon);

		processedMessageRepository.save(
			ProcessedMessage.builder().
				messageId(message.getMessageId())
				.build());

		log.info("Issued book coupon {} to member {} successfully", coupon.getId(), member.getId());
	}
}
