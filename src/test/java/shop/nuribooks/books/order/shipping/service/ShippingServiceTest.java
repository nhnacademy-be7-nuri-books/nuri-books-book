package shop.nuribooks.books.order.shipping.service;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.exception.shipping.ShippingPolicyNotFoundException;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.shipping.dto.ShippingRegisterRequest;
import shop.nuribooks.books.order.shipping.entity.Shipping;
import shop.nuribooks.books.order.shipping.entity.ShippingPolicy;
import shop.nuribooks.books.order.shipping.repository.ShippingPolicyRepository;
import shop.nuribooks.books.order.shipping.repository.ShippingRepository;
import shop.nuribooks.books.order.shipping.service.impl.ShippingServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ShippingServiceTest {
	@InjectMocks
	private ShippingServiceImpl shippingService;

	@Mock
	private ShippingPolicyRepository shippingPolicyRepository;

	@Mock
	private ShippingRepository shippingRepository;

	private ShippingPolicy shippingPolicy;
	private Shipping shipping;
	private ShippingRegisterRequest shippingRegisterRequest;
	private Order order;

	@BeforeEach
	void setUp() {
		Customer customer = TestUtils.createCustomer();
		order = TestUtils.createOrder(customer);
		shippingPolicy = TestUtils.createShippingPolicy();
		TestUtils.setIdForEntity(shippingPolicy, 1L);
		shipping = TestUtils.createShipping(order, shippingPolicy);
		shippingRegisterRequest = new ShippingRegisterRequest(shippingPolicy.getId(), shipping.getRecipientName(),
			shipping.getRecipientAddress(), shipping.getRecipientAddressDetail(), shipping.getRecipientZipcode(),
			shipping.getRecipientPhoneNumber(), shipping.getSenderName(), shipping.getSenderPhoneNumber());
	}

	@Test
	void registerTest() {
		when(shippingPolicyRepository.findClosedShippingPolicy(anyInt())).thenReturn(shippingPolicy);
		when(shippingRepository.save(any())).thenReturn(shipping);

		shippingService.registerShipping(order, shippingRegisterRequest);

		verify(shippingRepository).save(any());
	}

	@Test
	void registerTestFail() {
		when(shippingPolicyRepository.findClosedShippingPolicy(anyInt())).thenReturn(null);

		Assertions.assertThrows(ShippingPolicyNotFoundException.class,
			() -> shippingService.registerShipping(order, shippingRegisterRequest));
	}
}