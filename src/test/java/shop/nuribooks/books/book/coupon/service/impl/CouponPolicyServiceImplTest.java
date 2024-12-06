package shop.nuribooks.books.book.coupon.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.coupon.dto.CouponPolicyRequest;
import shop.nuribooks.books.book.coupon.dto.CouponPolicyResponse;
import shop.nuribooks.books.book.coupon.entity.CouponPolicy;
import shop.nuribooks.books.book.coupon.enums.DiscountType;
import shop.nuribooks.books.book.coupon.repository.CouponPolicyRepository;
import shop.nuribooks.books.exception.coupon.CouponPolicyAlreadyExistsException;
import shop.nuribooks.books.exception.coupon.CouponPolicyNotFoundException;

@ExtendWith(MockitoExtension.class)
class CouponPolicyServiceImplTest {
	@InjectMocks
	private CouponPolicyServiceImpl couponPolicyService;

	@Mock
	private CouponPolicyRepository couponPolicyRepository;

	private CouponPolicyRequest couponPolicyRequest;
	private CouponPolicy couponPolicy1;
	private CouponPolicy couponPolicy2;

	@BeforeEach
	void setUp() {
		couponPolicyRequest = new CouponPolicyRequest("테스트 쿠폰정책 1", DiscountType.FIXED, BigDecimal.valueOf(10000), null,
			5000);

		couponPolicy1 = new CouponPolicy("테스트 쿠폰정책 1", DiscountType.FIXED, BigDecimal.valueOf(10000), null, 5000);
		couponPolicy2 = new CouponPolicy("테스트 쿠폰정책 2", DiscountType.RATED, BigDecimal.valueOf(15000), null, 10);

	}

	@DisplayName("모든 쿠폰 정책 조회")
	@Test
	void getCouponPolicies() {
		List<CouponPolicy> couponPolicies = Arrays.asList(couponPolicy1, couponPolicy2);
		Pageable pageable = PageRequest.of(0, 10);
		Page<CouponPolicy> couponPolicyPage = new PageImpl<>(couponPolicies, pageable, couponPolicies.size());

		when(couponPolicyRepository.findAll(pageable)).thenReturn(couponPolicyPage);

		Page<CouponPolicyResponse> responsePage = couponPolicyService.getCouponPolicies(pageable);

		assertThat(responsePage.getTotalElements()).isEqualTo(2);
		assertThat(responsePage.getContent()).hasSize(2);
		assertThat(responsePage.getContent().get(0).name()).isEqualTo("테스트 쿠폰정책 1");
		assertThat(responsePage.getContent().get(1).name()).isEqualTo("테스트 쿠폰정책 2");
	}

	@DisplayName("해당 쿠폰 정책 조회")
	@Test
	void getCouponPolicy() {
		Long couponPolicyId = 1L;

		when(couponPolicyRepository.findById(couponPolicyId)).thenReturn(Optional.of(couponPolicy1));

		CouponPolicyResponse response = couponPolicyService.getCouponPolicy(1L);

		assertThat(response.id()).isEqualTo(couponPolicy1.getId());
		assertThat(response.name()).isEqualTo(couponPolicy1.getName());
	}

	@DisplayName("쿠폰 정책 등록")
	@Test
	void registerCouponPolicy() {
		when(couponPolicyRepository.findByName(couponPolicyRequest.name())).thenReturn(Optional.empty());
		when(couponPolicyRepository.save(any(CouponPolicy.class))).thenReturn(couponPolicy1);

		CouponPolicy savedCouponPolicy = couponPolicyService.registerCouponPolicy(couponPolicyRequest);

		assertThat(savedCouponPolicy.getName()).isEqualTo(couponPolicyRequest.name());
		assertThat(savedCouponPolicy.getDiscountType()).isEqualTo(couponPolicyRequest.discountType());
		verify(couponPolicyRepository, times(1)).save(any(CouponPolicy.class));
	}

	@DisplayName("쿠폰 정책 등록 실패 - 중복")
	@Test
	void failed_registerCouponPolicy() {
		when(couponPolicyRepository.findByName(couponPolicyRequest.name())).thenReturn(Optional.of(couponPolicy1));

		assertThrows(CouponPolicyAlreadyExistsException.class,
			() -> couponPolicyService.registerCouponPolicy(couponPolicyRequest));

	}

	@DisplayName("쿠폰 정책 업데이트")
	@Test
	void updateCouponPolicy() {
		when(couponPolicyRepository.findById(2L)).thenReturn(Optional.of(couponPolicy1));

		CouponPolicy updatedCouponPolicy = couponPolicyService.updateCouponPolicy(2L, couponPolicyRequest);

		assertThat(updatedCouponPolicy.getId()).isEqualTo(couponPolicy1.getId());
		assertThat(updatedCouponPolicy.getName()).isEqualTo(couponPolicyRequest.name());

	}

	@DisplayName("쿠폰 정책 업데이트 실패 - 존재x")
	@Test
	void failed_updateCouponPolicy() {
		when(couponPolicyRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(
			CouponPolicyNotFoundException.class, () -> couponPolicyService.updateCouponPolicy(1L, couponPolicyRequest));
	}

	@DisplayName("쿠폰 정책 삭제")
	@Test
	void deleteCouponPolicy() {
		when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(couponPolicy1));

		couponPolicyService.deleteCouponPolicy(1L);

		verify(couponPolicyRepository, times(1)).delete(couponPolicy1);
	}

	@DisplayName("쿠폰 정책 삭제 실패 - 존재x")
	@Test
	void failed_deleteCouponPolicy() {
		when(couponPolicyRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(CouponPolicyNotFoundException.class, () -> couponPolicyService.deleteCouponPolicy(1L));
	}

	@DisplayName("쿠폰 validation")
	@Test
	void validateCouponPolicyRequest() {
		couponPolicyService.validateCouponPolicyRequest(couponPolicyRequest);

	}
}