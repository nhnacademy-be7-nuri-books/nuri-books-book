package shop.nuribooks.books.order.shipping.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.exception.shipping.ShippingPolicyNotFoundException;
import shop.nuribooks.books.order.shipping.dto.ShippingPolicyRequest;
import shop.nuribooks.books.order.shipping.dto.ShippingPolicyResponse;
import shop.nuribooks.books.order.shipping.entity.ShippingPolicy;
import shop.nuribooks.books.order.shipping.repository.ShippingPolicyRepository;
import shop.nuribooks.books.order.shipping.service.ShippingPolicyService;

@RequiredArgsConstructor
@Service
public class ShippingPolicyServiceImpl implements ShippingPolicyService {
	private final ShippingPolicyRepository shippingPolicyRepository;

	@Override
	public Page<ShippingPolicyResponse> getShippingPolicyResponses(Pageable pageable) {
		return shippingPolicyRepository.findAll(pageable).map(ShippingPolicy::toResponseDto);
	}

	@Override
	public ShippingPolicy registerShippingPolicy(ShippingPolicyRequest shippingPolicyRequest) {
		return shippingPolicyRepository.save(shippingPolicyRequest.toEntity());
	}

	@Override
	public ShippingPolicy updateShippingPolicy(Long id, ShippingPolicyRequest shippingPolicyRequest) {
		ShippingPolicy shippingPolicy = shippingPolicyRepository.findById(id)
			.orElseThrow(ShippingPolicyNotFoundException::new);
		shippingPolicy.update(shippingPolicyRequest);
		return shippingPolicy;
	}

	@Override
	public void expireShippingPolicy(Long id) {
		ShippingPolicy shippingPolicy = shippingPolicyRepository.findById(id)
			.orElseThrow(ShippingPolicyNotFoundException::new);
		shippingPolicy.expire();
	}
}
