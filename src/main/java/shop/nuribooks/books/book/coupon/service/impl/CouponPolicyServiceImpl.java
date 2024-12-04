package shop.nuribooks.books.book.coupon.service.impl;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.coupon.dto.CouponPolicyRequest;
import shop.nuribooks.books.book.coupon.dto.CouponPolicyResponse;
import shop.nuribooks.books.book.coupon.entity.CouponPolicy;
import shop.nuribooks.books.book.coupon.enums.DiscountType;
import shop.nuribooks.books.book.coupon.repository.CouponPolicyRepository;
import shop.nuribooks.books.book.coupon.service.CouponPolicyService;
import shop.nuribooks.books.exception.coupon.CouponPolicyAlreadyExistsException;
import shop.nuribooks.books.exception.coupon.CouponPolicyNotFoundException;
import shop.nuribooks.books.exception.coupon.InvalidCouponException;

@RequiredArgsConstructor
@Service
public class CouponPolicyServiceImpl implements CouponPolicyService {

	private final CouponPolicyRepository couponPolicyRepository;

	/**
	 * 모든 쿠폰 정책 조회하는 메서드
	 * @param pageable
	 * @return
	 */
	@Override
	public Page<CouponPolicyResponse> getCouponPolicies(Pageable pageable) {
		Page<CouponPolicy> couponPolicies = couponPolicyRepository.findAll(pageable);
		return couponPolicies.map(CouponPolicyResponse::of);
	}

	/**
	 * 해당 id 쿠폰 정책 조회하는 메서드
	 * @param id
	 * @return
	 */
	@Override
	public CouponPolicyResponse getCouponPolicy(Long id) {
		CouponPolicy couponPolicy = couponPolicyRepository.findById(id)
			.orElseThrow(CouponPolicyNotFoundException::new);

		return CouponPolicyResponse.of(couponPolicy);
	}

	/**
	 * 쿠폰 정책 등록하는 메서드
	 * @param request
	 * @return
	 */
	@Override
	public CouponPolicy registerCouponPolicy(CouponPolicyRequest request) {

		Optional<CouponPolicy> existingCouponPolicy = couponPolicyRepository.findByName(request.name());
		if (existingCouponPolicy.isPresent()) {
			throw new CouponPolicyAlreadyExistsException();
		}

		validateCouponPolicyRequest(request);
		CouponPolicy couponPolicy = request.toEntity();
		return couponPolicyRepository.save(couponPolicy);
	}

	/**
	 * 쿠폰 정책 수정하는 메서드
	 * @param id
	 * @param request
	 * @return
	 */
	@Transactional
	@Override
	public CouponPolicy updateCouponPolicy(Long id, CouponPolicyRequest request) {
		CouponPolicy couponPolicy = couponPolicyRepository.findById(id)
			.orElseThrow(CouponPolicyNotFoundException::new);
		validateCouponPolicyRequest(request);
		couponPolicy.update(request);
		return couponPolicy;
	}

	/**
	 * 쿠폰 정책 삭제하는 메서드
	 * @param id
	 */
	@Transactional
	@Override
	public void deleteCouponPolicy(Long id) {
		CouponPolicy couponPolicy = couponPolicyRepository.findById(id)
			.orElseThrow(CouponPolicyNotFoundException::new);

		couponPolicyRepository.delete(couponPolicy);
	}

	public void validateCouponPolicyRequest(CouponPolicyRequest request) {
		validateDiscountPolicy(request.discountType(), request.discount());
		if (request.maximumDiscountPrice() != null) {
			validateOrderPriceConsistency(request.minimumOrderPrice(), request.maximumDiscountPrice(),
				request.discountType(), request.discount());
		}
	}

	private void validateDiscountPolicy(DiscountType discountType, Integer discount) {
		if (discountType == DiscountType.RATED && (discount <= 0 || discount > 100)) {
			throw new InvalidCouponException("비율은 1~100 사이의 값이어야 합니다.");
		}
	}

	private void validateOrderPriceConsistency(BigDecimal minimumOrderPrice, BigDecimal maximumDiscountPrice,
		DiscountType discountType, int discount) {
		if (maximumDiscountPrice.compareTo(minimumOrderPrice) > 0) {
			throw new InvalidCouponException("최대 할인 금액은 최소 주문 금액보다 작거나 같아야 합니다.");
		}

		if (discountType.equals(DiscountType.FIXED)
			&& BigDecimal.valueOf(discount).compareTo(maximumDiscountPrice) < 0) {
			throw new InvalidCouponException("최대 할인 금액은 할인 금액보다 작을 수 없습니다.");
		}

	}
}
