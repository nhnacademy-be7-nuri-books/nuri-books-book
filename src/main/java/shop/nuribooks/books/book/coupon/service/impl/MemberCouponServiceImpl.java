package shop.nuribooks.books.book.coupon.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.book.coupon.dto.MemberCouponIssueRequest;
import shop.nuribooks.books.book.coupon.dto.MemberCouponOrderDto;
import shop.nuribooks.books.book.coupon.dto.MemberCouponResponse;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.entity.MemberCoupon;
import shop.nuribooks.books.book.coupon.message.BookCouponIssueMessage;
import shop.nuribooks.books.book.coupon.repository.CouponRepository;
import shop.nuribooks.books.book.coupon.repository.MemberCouponRepository;
import shop.nuribooks.books.book.coupon.service.MemberCouponService;
import shop.nuribooks.books.common.config.rabbitmq.RabbitmqConfig;
import shop.nuribooks.books.exception.coupon.CouponAlreadyIssuedException;
import shop.nuribooks.books.exception.coupon.CouponNotFoundException;
import shop.nuribooks.books.exception.member.MemberCartNotFoundException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;

/**
 * 쿠폰 발급 관련 작업을 처리하는 서비스.
 *
 * @author janghyun
 **/
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberCouponServiceImpl implements MemberCouponService {
	private final MemberRepository memberRepository;
	private final MemberCouponRepository memberCouponRepository;
	private final CouponRepository couponRepository;
	private final RabbitTemplate rabbitTemplate;

	/**
	 * 회원에게 쿠폰을 등록합니다.
	 *
	 * @param memberCouponIssueRequest 쿠폰 등록을 위한 정보를 포함한 요청
	 * @throws CouponNotFoundException 존재하지 않는 쿠폰일 경우 예외 발생
	 */
	@Transactional
	@Override
	public void registerMemberCoupon(MemberCouponIssueRequest memberCouponIssueRequest) {
		Coupon coupon = couponRepository.findById(memberCouponIssueRequest.couponId())
			.orElseThrow(CouponNotFoundException::new);
		Member member = memberRepository.findById(memberCouponIssueRequest.memberId())
			.orElseThrow(() -> new MemberNotFoundException("멤버를 못찾아요."));

		boolean alreadyIssued = memberCouponRepository.existsByMemberAndCoupon(member, coupon);
		if (alreadyIssued) {
			throw new CouponAlreadyIssuedException();
		}

		MemberCoupon memberCoupon = MemberCoupon.builder()
			.coupon(coupon)
			.member(member)
			.build();
		memberCouponRepository.save(memberCoupon);
	}

	/**
	 * 회원 ID를 기준으로 모든 쿠폰을 조회합니다.
	 *
	 * @param memberId 회원 ID
	 * @return 회원의 모든 쿠폰 정보를 담은 MemberCouponResponse 리스트
	 */
	@Override
	public List<MemberCouponResponse> getAllCouponByMemberId(Long memberId) {
		return memberCouponRepository.findAllByMemberId(memberId);
	}

	/**
	 * 쿠폰 사용 상태를 업데이트합니다.
	 *
	 * @param memberCouponId 업데이트할 회원 쿠폰 ID
	 * @throws CouponNotFoundException 존재하지 않는 쿠폰일 경우 예외 발생
	 */
	@Override
	@Transactional
	public void updateIsUsed(Long memberCouponId) {
		MemberCoupon coupon = memberCouponRepository.findById(memberCouponId)
			.orElseThrow(CouponNotFoundException::new);
		coupon.setUsed(true);
	}

	/**
	 * 회원 쿠폰을 삭제합니다.
	 *
	 * @param memberCouponId 삭제할 회원 쿠폰 ID
	 * @throws CouponNotFoundException 존재하지 않는 쿠폰일 경우 예외 발생
	 */
	@Transactional
	@Override
	public void deleteMemberCoupon(Long memberCouponId) {
		MemberCoupon memberCoupon = memberCouponRepository.findById(memberCouponId)
			.orElseThrow(CouponNotFoundException::new);
		memberCouponRepository.delete(memberCoupon);
	}

	@Override
	public Page<MemberCouponResponse> getAvailableCouponsByMemberId(Long memberId, Pageable pageable) {
		return memberCouponRepository.findAvailableCouponsByMemberId(memberId, pageable);
	}

	@Override
	public Page<MemberCouponResponse> getExpiredOrUsedCouponsByMemberId(Long memberId, Pageable pageable) {
		return memberCouponRepository.findExpiredOrUsedCouponsByMemberId(memberId, pageable);
	}

	@Override
	public List<MemberCouponOrderDto> getAllTypeAvailableCouponsByMemberId(Long memberId, BigDecimal orderTotalPrice) {
		return memberCouponRepository.findAllTypeAvailableCouponsByMemberId(memberId, orderTotalPrice);
	}

	@Override
	public MemberCouponOrderDto getMemberCoupon(Long memberCouponId) {
		MemberCoupon memberCoupon = memberCouponRepository.findById(memberCouponId)
			.orElseThrow(MemberCartNotFoundException::new);

		return MemberCouponOrderDto.builder()
			.couponId(memberCoupon.getId())
			.couponName(memberCoupon.getCoupon().getName())
			.discountType(memberCoupon.getCoupon().getCouponPolicy().getDiscountType())
			.discount(memberCoupon.getCoupon().getCouponPolicy().getDiscount())
			.minimumOrderPrice(memberCoupon.getCoupon().getCouponPolicy().getMinimumOrderPrice())
			.maximumDiscountPrice(memberCoupon.getCoupon().getCouponPolicy().getMaximumDiscountPrice())
			.isUsed(memberCoupon.isUsed())
			.createdAt(memberCoupon.getCreatedAt())
			.expiredAt(memberCoupon.getExpiredAt())
			.build();
	}

	@Override
	public void publishBookCouponIssue(Long memberId, Long couponId) {
		BookCouponIssueMessage message = BookCouponIssueMessage
			.builder()
			.couponId(couponId)
			.memberId(memberId)
			.messageId("book-coupon-" + UUID.randomUUID())
			.build();

		try {
			rabbitTemplate.convertAndSend(
				RabbitmqConfig.BOOK_COUPON_ISSUE_EXCHANGE,
				RabbitmqConfig.BOOK_COUPON_ISSUE_ROUTING_KEY,
				message
			);
		} catch (AmqpRejectAndDontRequeueException e) {
			log.error("Invalid Message Format : {}", e.getMessage());
		}
	}
}
