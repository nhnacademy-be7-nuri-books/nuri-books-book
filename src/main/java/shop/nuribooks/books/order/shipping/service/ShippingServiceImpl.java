package shop.nuribooks.books.order.shipping.service;

import java.math.BigDecimal;
import java.util.Objects;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.exception.shipping.ShippingPolicyNotFoundException;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.shipping.dto.ShippingRegisterRequest;
import shop.nuribooks.books.order.shipping.entity.Shipping;
import shop.nuribooks.books.order.shipping.entity.ShippingPolicy;
import shop.nuribooks.books.order.shipping.repository.ShippingPolicyRepository;
import shop.nuribooks.books.order.shipping.repository.ShippingRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingServiceImpl implements ShippingService {

	private final ShippingPolicyRepository shippingPolicyRepository;
	private final ShippingRepository shippingRepository;

	@Override
	public void registerShipping(Order order, ShippingRegisterRequest shippingRegisterRequest) {

		ShippingPolicy shippingPolicy = shippingPolicyRepository.findClosedShippingPolicy(order.getPaymentPrice().intValue());

		if(Objects.isNull(shippingPolicy)){
			throw  new ShippingPolicyNotFoundException();
		};

		Shipping shipping = Shipping.builder()
			.order(order)
			.shippingPolicy(shippingPolicy)
			.recipientName(shippingRegisterRequest.recipientName())
			.recipientAddress(shippingRegisterRequest.recipientAddress())
			.recipientAddressDetail(shippingRegisterRequest.recipientAddressDetail())
			.recipientZipcode(shippingRegisterRequest.recipientZipcode())
			.recipientPhoneNumber(shippingRegisterRequest.recipientPhoneNumber())
			.senderName(shippingRegisterRequest.senderName())
			.senderPhoneNumber(shippingRegisterRequest.senderPhoneNumber())
			.build();

			shippingRepository.save(shipping);

	}
}
