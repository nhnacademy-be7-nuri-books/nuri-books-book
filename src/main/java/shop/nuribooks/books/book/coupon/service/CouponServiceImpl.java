package shop.nuribooks.books.book.coupon.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.coupon.dto.CouponRequest;
import shop.nuribooks.books.book.coupon.dto.CouponResponse;
import shop.nuribooks.books.book.coupon.dto.MemberCouponIssueRequest;
import shop.nuribooks.books.book.coupon.entity.Coupon;
import shop.nuribooks.books.book.coupon.entity.CouponPolicy;
import shop.nuribooks.books.book.coupon.enums.CouponType;
import shop.nuribooks.books.book.coupon.enums.ExpirationType;
import shop.nuribooks.books.book.coupon.enums.IssuanceType;
import shop.nuribooks.books.book.coupon.mapper.CouponMapper;
import shop.nuribooks.books.book.coupon.repository.CouponPolicyRepository;
import shop.nuribooks.books.book.coupon.repository.CouponRepository;
import shop.nuribooks.books.book.coupon.strategy.CouponStrategy;
import shop.nuribooks.books.book.coupon.strategy.CouponStrategyFactory;
import shop.nuribooks.books.exception.coupon.CouponAlreadyExistsException;
import shop.nuribooks.books.exception.coupon.CouponNotFoundException;
import shop.nuribooks.books.exception.coupon.InvalidCouponException;
import shop.nuribooks.books.member.member.entity.Member;

@RequiredArgsConstructor
@Service
public class CouponServiceImpl implements CouponService {
	private final CouponRepository couponRepository;
	private final MemberCouponService memberCouponService;
	private final CouponPolicyRepository couponPolicyRepository;
	private final CouponStrategyFactory couponStrategyFactory;
	private final CouponMapper couponMapper;

	/**
	 * 쿠폰 등록하는 메서드
	 *
	 * @param request
	 * @return
	 */
	@Override
	public Coupon registerCoupon(CouponRequest request) {
		validateCouponRequest(request);

		if (couponRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(request.name())) {
			throw new CouponAlreadyExistsException();
		}

		CouponPolicy couponPolicy = couponPolicyRepository.findById(request.couponPolicyId()).orElseThrow(
			CouponNotFoundException::new);

		CouponStrategy couponStrategy = couponStrategyFactory.getStrategy(request.couponType());

		Coupon coupon = couponStrategy.registerCoupon(request, couponPolicy);

		return couponRepository.save(coupon);
	}

	/**
	 * 쿠폰 타입별로 조회하는 메서드
	 *
	 * @param pageable
	 * @return
	 */
	@Override
	public Page<CouponResponse> getCoupons(CouponType type, Pageable pageable) {
		Page<Coupon> coupons = couponRepository.findCouponsByCouponType(pageable, type);
		return coupons.map(couponMapper::toDto);
	}

	/**
	 * 모든 쿠폰 조회하는 메서드
	 * @param pageable
	 * @return
	 */
	@Override
	public Page<CouponResponse> getAllCoupons(Pageable pageable) {
		Page<Coupon> coupons = couponRepository.findAll(pageable);
		return coupons.map(couponMapper::toDto);
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
		return couponMapper.toDto(coupon);
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
	@Transactional(propagation = Propagation.REQUIRES_NEW)
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

		coupon.setDeletedAt();
	}

	public void validateCouponRequest(CouponRequest couponRequest) {
		validateExpiration(couponRequest.expirationType(), couponRequest.period(), couponRequest.expiredAt());
		validateIssuanceTypeAndQuantity(couponRequest.issuanceType(), couponRequest.quantity());
	}

	private void validateExpiration(ExpirationType expirationType, Integer expirationDays, LocalDate expiredAt) {
		if (expirationType == null) {
			throw new InvalidCouponException("ExpirationType은 필수 값입니다.");
		}

		switch (expirationType) {
			case ExpirationType.DATE -> validateDateExpiration(expiredAt);
			case ExpirationType.DAYS -> validateDaysExpiration(expirationDays);
			default -> throw new InvalidCouponException("알 수 없는 ExpirationType입니다.");

		}
	}

	private void validateDateExpiration(LocalDate expiredAt) {
		if (expiredAt == null) {
			throw new InvalidCouponException("DATE 타입의 쿠폰은 expiredAt이 필수 값입니다.");
		}
		if (expiredAt.isBefore(LocalDate.now())) {
			throw new InvalidCouponException("DATE 타입의 쿠폰은 유효한 expiredAt이 필요합니다.");
		}
	}

	private void validateDaysExpiration(Integer expirationDays) {
		if (expirationDays == null || expirationDays <= 0) {
			throw new InvalidCouponException("DAYS 타입의 ExpirationDays는 1 이상의 값이어야 합니다.");
		}
	}

	private void validateIssuanceTypeAndQuantity(IssuanceType issuanceType, Integer quantity) {
		if (issuanceType == IssuanceType.LIMITED && (quantity == null || quantity <= 0)) {
			throw new InvalidCouponException("LIMITED 타입 쿠폰은 1 이상의 quantity가 필요합니다.");
		}
		if (issuanceType == IssuanceType.UNLIMITED && quantity != null) {
			throw new InvalidCouponException("UNLIMITED 타입 쿠폰은 quantity가 null이어야 합니다.");
		}
	}

}
