package shop.nuribooks.books.order.shipping.service;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.exception.shipping.ShippingPolicyNotFoundException;
import shop.nuribooks.books.order.shipping.dto.ShippingPolicyRequest;
import shop.nuribooks.books.order.shipping.dto.ShippingPolicyResponse;
import shop.nuribooks.books.order.shipping.entity.ShippingPolicy;
import shop.nuribooks.books.order.shipping.repository.ShippingPolicyRepository;
import shop.nuribooks.books.order.shipping.service.impl.ShippingPolicyServiceImpl;

@ExtendWith(MockitoExtension.class)
class ShippingPolicyServiceTest {
	@InjectMocks
	ShippingPolicyServiceImpl shippingPolicyService;

	@Mock
	ShippingPolicyRepository shippingPolicyRepository;

	private ShippingPolicy shippingPolicy;
	private ShippingPolicyRequest shippingPolicyRequest;

	@BeforeEach
	void setUp() {
		shippingPolicy = TestUtils.createShippingPolicy();
		shippingPolicyRequest = new ShippingPolicyRequest(2000, null, BigDecimal.valueOf(20000));
	}

	@Test
	void getTest() {
		Pageable pageable = PageRequest.of(0, 3);
		Page<ShippingPolicy> pages = new PageImpl<>(List.of(shippingPolicy), pageable, 1);
		when(shippingPolicyRepository.findAll(pageable)).thenReturn(pages);

		Page<ShippingPolicyResponse> result = shippingPolicyService.getShippingPolicyResponses(pageable);

		Assertions.assertEquals(1, result.getTotalPages());
		Assertions.assertEquals(pageable.getPageSize(), result.getSize());
		Assertions.assertEquals(pageable.getPageNumber(), result.getNumber());
		Assertions.assertIterableEquals(List.of(shippingPolicy.toResponseDto()),
			result.getContent());
	}

	@Test
	void registerTest() {
		when(shippingPolicyRepository.save(any())).thenReturn(shippingPolicy);

		ShippingPolicy result = shippingPolicyService.registerShippingPolicy(shippingPolicyRequest);

		Assertions.assertEquals(shippingPolicy, result);
	}

	@Test
	void updateTest() {
		when(shippingPolicyRepository.findById(anyLong())).thenReturn(Optional.of(shippingPolicy));

		ShippingPolicy result = shippingPolicyService.updateShippingPolicy(1L, shippingPolicyRequest);

		Assertions.assertEquals(shippingPolicy, result);
	}

	@Test
	void updateFailTest() {
		when(shippingPolicyRepository.findById(anyLong())).thenReturn(Optional.empty());

		Assertions.assertThrows(ShippingPolicyNotFoundException.class,
			() -> shippingPolicyService.updateShippingPolicy(1L, shippingPolicyRequest));
	}

	@Test
	void deleteTest() {
		when(shippingPolicyRepository.findById(anyLong())).thenReturn(Optional.of(shippingPolicy));

		shippingPolicyService.expireShippingPolicy(1L);

		Assertions.assertNotNull(shippingPolicy.getExpiration());
	}

	@Test
	void deleteFailTest() {
		when(shippingPolicyRepository.findById(anyLong())).thenReturn(Optional.empty());

		Assertions.assertThrows(ShippingPolicyNotFoundException.class,
			() -> shippingPolicyService.expireShippingPolicy(1L));
	}
}
