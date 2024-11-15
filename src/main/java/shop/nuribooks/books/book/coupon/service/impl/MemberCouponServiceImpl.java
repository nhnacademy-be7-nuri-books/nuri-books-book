package shop.nuribooks.books.book.coupon.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.coupon.dto.MemberCouponRegisterRequest;
import shop.nuribooks.books.book.coupon.dto.MemberCouponResponse;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.entity.MemberCoupon;
import shop.nuribooks.books.book.coupon.repository.CouponRepository;
import shop.nuribooks.books.book.coupon.repository.MemberCouponRepository;
import shop.nuribooks.books.book.coupon.service.MemberCouponService;
import shop.nuribooks.books.exception.coupon.CouponNotFoundException;

/**
 * 쿠폰 발급 관련 작업을 처리하는 서비스.
 *
 * @author janghyun
 **/
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberCouponServiceImpl implements MemberCouponService {

	private final MemberCouponRepository memberCouponRepository;
	private final CouponRepository couponRepository;

	/**
	 * 회원에게 쿠폰을 등록합니다.
	 *
	 * @param memberCouponRegisterRequest 쿠폰 등록을 위한 정보를 포함한 요청
	 * @throws CouponNotFoundException 존재하지 않는 쿠폰일 경우 예외 발생
	 */
	@Override
	public void registerMemberCoupon(MemberCouponRegisterRequest memberCouponRegisterRequest) {
		Coupon coupon = couponRepository.findById(memberCouponRegisterRequest.couponId())
			.orElseThrow(() -> new CouponNotFoundException("존재하지 않는 쿠폰입니다."));

		MemberCoupon memberCoupon = MemberCoupon.builder()
			.coupon(coupon)
			.memberId(memberCouponRegisterRequest.memberId())
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
	public void updateIsUsed(Long memberCouponId) {
		MemberCoupon coupon = memberCouponRepository.findById(memberCouponId)
			.orElseThrow(() -> new CouponNotFoundException("존재하지 않는 쿠폰입니다."));
		coupon.setUsed();
	}

	/**
	 * 회원 쿠폰을 삭제합니다.
	 *
	 * @param memberCouponId 삭제할 회원 쿠폰 ID
	 * @throws CouponNotFoundException 존재하지 않는 쿠폰일 경우 예외 발생
	 */
	@Override
	public void deleteMemberCoupon(Long memberCouponId) {
		MemberCoupon memberCoupon = memberCouponRepository.findById(memberCouponId)
			.orElseThrow(() -> new CouponNotFoundException("존재하지 않는 쿠폰입니다."));
		memberCouponRepository.delete(memberCoupon);
	}

}
