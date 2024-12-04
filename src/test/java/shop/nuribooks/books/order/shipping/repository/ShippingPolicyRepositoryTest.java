package shop.nuribooks.books.order.shipping.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import shop.nuribooks.books.common.config.QuerydslConfiguration;
import shop.nuribooks.books.order.shipping.entity.ShippingPolicy;

@DataJpaTest
@Import({QuerydslConfiguration.class})
class ShippingPolicyRepositoryTest {

	@Autowired
	private ShippingPolicyRepository shippingPolicyRepository;

	@Test
	void findClosedShippingPolicyTest() {
		ShippingPolicy shippingPolicy1 = shippingPolicyRepository.save(
			new ShippingPolicy(1000, null, BigDecimal.valueOf(10000)));

		shippingPolicyRepository.save(new ShippingPolicy(1000, LocalDateTime.now(), BigDecimal.valueOf(15000)));

		ShippingPolicy result = shippingPolicyRepository.findClosedShippingPolicy(15000);
		Assertions.assertEquals(shippingPolicy1, result);
	}
}
