package shop.nuribooks.books.order.shipping.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.order.shipping.dto.ShippingResponse;
import shop.nuribooks.books.order.shipping.entity.Shipping;
import shop.nuribooks.books.order.shipping.exception.ShippingNotFoundException;
import shop.nuribooks.books.order.shipping.repository.ShippingRepository;
import shop.nuribooks.books.order.shipping.service.ShippingAdminService;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ShippingAdminServiceImpl implements ShippingAdminService {
	private final ShippingRepository shippingRepository;

	@Override
	public Page<ShippingResponse> getShippingResponses(Pageable pageable) {
		return shippingRepository.findAll(pageable).map(Shipping::toResponseDto);
	}

	@Override
	public ShippingResponse getShippingResponse(Long id) {
		return shippingRepository.findById(id).orElseThrow(ShippingNotFoundException::new).toResponseDto();
	}
}
