package shop.nuribooks.books.book.coupon.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.coupon.dto.CouponPolicyRequest;
import shop.nuribooks.books.book.coupon.dto.CouponPolicyResponse;
import shop.nuribooks.books.book.coupon.entity.CouponPolicy;
import shop.nuribooks.books.book.coupon.repository.CouponPolicyRepository;
import shop.nuribooks.books.exception.coupon.CouponPolicyAlreadyExistsException;

@RequiredArgsConstructor
@Service
public class CouponPolicyServiceImpl implements CouponPolicyService {

	private final CouponPolicyRepository couponPolicyRepository;

	/**
	 * 쿠폰 정책 등록하는 메서드
	 *
	 * @param couponPolicyRequest
	 * @return
	 */
	@Override
	public CouponPolicy registerCouponPolicy(CouponPolicyRequest couponPolicyRequest) {
		if (couponPolicyRepository.existsByNameIgnoreCase(couponPolicyRequest.name())) {
			throw new CouponPolicyAlreadyExistsException("같은 이름의 쿠폰 정책이 이미 존재합니다.");
		}

		CouponPolicy couponPolicy = couponPolicyRequest.toEntity();
		return couponPolicyRepository.save(couponPolicy);
	}
}
