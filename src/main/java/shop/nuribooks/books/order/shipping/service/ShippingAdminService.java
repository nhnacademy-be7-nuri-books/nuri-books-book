package shop.nuribooks.books.order.shipping.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.order.shipping.dto.ShippingResponse;

public interface ShippingAdminService {
	Page<ShippingResponse> getShippingResponses(Pageable pageable);

	ShippingResponse getShippingResponse(Long id);

	void updateDeliveryStatus(Long id);
}
