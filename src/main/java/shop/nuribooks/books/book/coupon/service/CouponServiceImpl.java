package shop.nuribooks.books.book.coupon.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.coupon.dto.CouponRequest;
import shop.nuribooks.books.book.coupon.dto.CouponResponse;
import shop.nuribooks.books.book.coupon.dto.MemberCouponIssueRequest;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.enums.CouponType;
import shop.nuribooks.books.book.coupon.repository.CouponRepository;
import shop.nuribooks.books.exception.coupon.CouponAlreadyExistsException;
import shop.nuribooks.books.exception.coupon.CouponNotFoundException;
import shop.nuribooks.books.member.member.entity.Member;

@RequiredArgsConstructor
@Service
public class CouponServiceImpl implements CouponService {
	private final CouponRepository couponRepository;
	private final MemberCouponService memberCouponService;

	/**
	 * 쿠폰 등록하는 메서드
	 *
	 * @param request
	 * @return
	 */
	@Override
	public Coupon registerCoupon(CouponRequest request) {
		if (couponRepository.existsByNameIgnoreCaseAndExpiredDateIsNull(request.name())) {
			throw new CouponAlreadyExistsException();
		}

		Coupon coupon = request.toEntity();
		return couponRepository.save(coupon);
	}

	/**
	 * 모든 쿠폰 조회하는 메서드
	 *
	 * @param pageable
	 * @return
	 */
	@Override
	public Page<CouponResponse> getCoupons(CouponType type, Pageable pageable) {
		return couponRepository.findCouponsByCouponId(pageable, type);
	}

	/**
	 * 쿠폰 조회하는 메서드
	 *
	 * @param id
	 * @return
	 */
	@Override
	public CouponResponse getCouponById(Long id) {
		Coupon coupon = couponRepository.findById(id)
			.orElseThrow(CouponNotFoundException::new);

		return CouponResponse.of(coupon);
	}

	/**
	 * 쿠폰 정보 업데이트하는 메서드
	 *
	 * @param id
	 * @param request
	 * @return
	 */
	@Override
	@Transactional
	public Coupon updateCoupon(Long id, CouponRequest request) {
		Coupon coupon = couponRepository.findById(id)
			.orElseThrow(CouponNotFoundException::new);

		coupon.update(request);
		return coupon;
	}

	/**
	 * 웰컴 쿠폰 발급하는 메서드
	 *
	 * @param member
	 */
	@Override
	public void issueWelcomeCoupon(Member member) {
		Coupon welcomeCoupon = couponRepository.findCouponsByNameLike("%WELCOME%");

		MemberCouponIssueRequest request = new MemberCouponIssueRequest(member.getId(), welcomeCoupon.getId());
		memberCouponService.registerMemberCoupon(request);
	}

	/**
	 * 쿠폰 비활성화 메서드
	 *
	 * @param id
	 */
	@Override
	@Transactional
	public void expireCoupon(Long id) {
		Coupon coupon = couponRepository.findById(id)
			.orElseThrow(CouponNotFoundException::new);

		coupon.expire();
	}

}
