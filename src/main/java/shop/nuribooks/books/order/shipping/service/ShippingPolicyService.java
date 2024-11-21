package shop.nuribooks.books.order.shipping.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.order.shipping.dto.ShippingPolicyRequest;
import shop.nuribooks.books.order.shipping.dto.ShippingPolicyResponse;
import shop.nuribooks.books.order.shipping.entity.ShippingPolicy;

public interface ShippingPolicyService {
	/**
	 * 배송 정책 목록 가져오기
	 * @return
	 */
	Page<ShippingPolicyResponse> getShippingPolicyResponses(Pageable pageable);

	/**
	 * 배송 정책 등록
	 * @param shippingPolicyRequest
	 * @return
	 */
	ShippingPolicy registerShippingPolicy(ShippingPolicyRequest shippingPolicyRequest);

	/**
	 * 배송 정책 업데이트
	 * @param id
	 * @param shippingPolicyRequest
	 * @return
	 */
	ShippingPolicy updateShippingPolicy(Long id, ShippingPolicyRequest shippingPolicyRequest);

	/**
	 * 배송 정책 만료 처리
	 * @param id
	 */
	void expireShippingPolicy(Long id);
}
