package shop.nuribooks.books.order.order.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

import shop.nuribooks.books.member.customer.entity.Customer;

class OrderTest {

	@Test
	@DisplayName("주문 생성 테스트")
	void testOrderBuilder() {

		Customer customer = Customer.builder()
			.name("정누리")
			.password("Passw@rd1")
			.phoneNumber("01012345678")
			.email("nuri1234@example.com")
			.build();

		BigDecimal paymentPrice = new BigDecimal("50000");
		LocalDateTime orderedAt = LocalDateTime.of(2024, 11, 10, 14, 30);
		BigDecimal wrappingPrice = new BigDecimal("2000");
		LocalDate expectedDeliveryAt = LocalDate.of(2024, 11, 11);

		Order order = Order.builder()
			.customer(customer)
			.paymentPrice(paymentPrice)
			.orderedAt(orderedAt)
			.wrappingPrice(wrappingPrice)
			.expectedDeliveryAt(expectedDeliveryAt)
			.build();

		assertNotNull(customer);
		assertEquals(paymentPrice, order.getPaymentPrice());
		assertEquals(orderedAt, order.getOrderedAt());
		assertEquals(wrappingPrice, order.getWrappingPrice());
		assertEquals(expectedDeliveryAt, order.getExpectedDeliveryAt());
	}

	@Test
	@DisplayName("포장 값의 기본값이 0가 되는 지 테스트")
	void testWrappingPriceDefaultValue() {
		Customer customer = Customer.builder()
			.name("정누리")
			.password("Passw@rd1")
			.phoneNumber("01012345678")
			.email("nuri1234@example.com")
			.build();

		BigDecimal paymentPrice = new BigDecimal("50000");
		LocalDateTime orderedAt = LocalDateTime.of(2024, 11, 10, 14, 30);
		LocalDate expectedDeliveryAt = LocalDate.of(2024, 11, 11);

		Order order = Order.builder()
			.customer(customer)
			.paymentPrice(paymentPrice)
			.orderedAt(orderedAt)
			.expectedDeliveryAt(expectedDeliveryAt)
			.build();

		assertNotNull(order.getWrappingPrice());
		assertEquals(BigDecimal.ZERO, order.getWrappingPrice());
	}

}
