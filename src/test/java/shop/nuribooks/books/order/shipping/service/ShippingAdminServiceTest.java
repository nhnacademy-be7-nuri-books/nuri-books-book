package shop.nuribooks.books.order.shipping.service;

import static org.mockito.Mockito.*;

import java.util.LinkedList;
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

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.orderdetail.entity.OrderDetail;
import shop.nuribooks.books.order.orderdetail.entity.OrderState;
import shop.nuribooks.books.order.orderdetail.repository.OrderDetailRepository;
import shop.nuribooks.books.order.shipping.dto.ShippingResponse;
import shop.nuribooks.books.order.shipping.entity.Shipping;
import shop.nuribooks.books.order.shipping.entity.ShippingPolicy;
import shop.nuribooks.books.order.shipping.exception.CanNotDeliveryException;
import shop.nuribooks.books.order.shipping.exception.ShippingNotFoundException;
import shop.nuribooks.books.order.shipping.repository.ShippingRepository;
import shop.nuribooks.books.order.shipping.service.impl.ShippingAdminServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ShippingAdminServiceTest {
	@InjectMocks
	ShippingAdminServiceImpl shippingAdminService;
	@Mock
	ShippingRepository shippingRepository;
	@Mock
	OrderDetailRepository orderDetailRepository;
	private Book book;
	private Order order;
	private Shipping shipping;
	private List<OrderDetail> orderDetails;

	@BeforeEach
	void setUp() {
		Customer customer = TestUtils.createCustomer();
		order = TestUtils.createOrder(customer);
		TestUtils.setIdForEntity(order, 1L);
		book = TestUtils.createBook(TestUtils.createPublisher());
		OrderDetail orderDetail = TestUtils.createOrderDetail(order, book);
		orderDetail.setOrderState(OrderState.PAID);
		orderDetails = new LinkedList<>();
		orderDetails.add(orderDetail);
		ShippingPolicy shippingPolicy = TestUtils.createShippingPolicy();
		shipping = TestUtils.createShipping(order, shippingPolicy);
		TestUtils.setIdForEntity(shipping, 1L);
	}

	@Test
	void getShippingResTest() {
		Pageable pageable = PageRequest.of(0, 3);
		when(shippingRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(shipping), pageable, 1));

		Page<ShippingResponse> pages = shippingAdminService.getShippingResponses(pageable);

		Assertions.assertEquals(0, pages.getNumber());
		Assertions.assertEquals(3, pages.getSize());
		Assertions.assertEquals(1, pages.getTotalElements());
		Assertions.assertIterableEquals(List.of(shipping.toResponseDto()), pages.getContent());
	}

	@Test
	void getShippingResOneTest() {
		when(shippingRepository.findById(anyLong())).thenReturn(Optional.of(shipping));

		Assertions.assertEquals(shipping.toResponseDto(), shippingAdminService.getShippingResponse(shipping.getId()));
	}

	@Test
	void getShippingResOneTestFail() {
		when(shippingRepository.findById(anyLong())).thenReturn(Optional.empty());

		Assertions.assertThrows(ShippingNotFoundException.class,
			() -> shippingAdminService.getShippingResponse(111L));
	}

	@Test
	void updateShippingResTest() {
		when(shippingRepository.findById(anyLong())).thenReturn(Optional.of(shipping));
		when(orderDetailRepository.findByOrderId(anyLong())).thenReturn(orderDetails);
		shippingAdminService.updateDeliveryStatus(shipping.getId());

		Assertions.assertNotNull(shipping.getOrderInvoiceNumber());
		Assertions.assertNotNull(shipping.getShippingAt());

		for (OrderDetail orderDetail : orderDetails) {
			Assertions.assertEquals(OrderState.DELIVERING, orderDetail.getOrderState());
		}
	}

	@Test
	void updateShippingResFailTest() {
		when(shippingRepository.findById(anyLong())).thenReturn(Optional.empty());
		Assertions.assertThrows(ShippingNotFoundException.class,
			() -> shippingAdminService.updateDeliveryStatus(shipping.getId()));
	}

	@Test
	void updateShippingResTest_OrderStateWrong() {
		when(shippingRepository.findById(anyLong())).thenReturn(Optional.of(shipping));
		OrderDetail wrongOrderDetail = TestUtils.createOrderDetail(order, book);
		wrongOrderDetail.setOrderState(OrderState.PENDING);
		orderDetails.add(wrongOrderDetail);
		when(orderDetailRepository.findByOrderId(anyLong())).thenReturn(orderDetails);
		Assertions.assertThrows(CanNotDeliveryException.class,
			() -> shippingAdminService.updateDeliveryStatus(shipping.getId()));
	}

	@Test
	void completeShippingResTest() {
		when(shippingRepository.findById(anyLong())).thenReturn(Optional.of(shipping));
		orderDetails.getFirst().setOrderState(OrderState.DELIVERING);
		when(orderDetailRepository.findByOrderId(anyLong())).thenReturn(orderDetails);
		shippingAdminService.completeDelivery(shipping.getId());

		Assertions.assertNotNull(shipping.getOrderInvoiceNumber());
		Assertions.assertNotNull(shipping.getShippingCompletedAt());

		for (OrderDetail orderDetail : orderDetails) {
			Assertions.assertEquals(OrderState.COMPLETED, orderDetail.getOrderState());
		}
	}

	@Test
	void completeShippingResFailTest() {
		when(shippingRepository.findById(anyLong())).thenReturn(Optional.empty());
		Assertions.assertThrows(ShippingNotFoundException.class,
			() -> shippingAdminService.completeDelivery(shipping.getId()));
	}

	@Test
	void completeShippingResTest_OrderStateWrong() {
		when(shippingRepository.findById(anyLong())).thenReturn(Optional.of(shipping));
		orderDetails.getFirst().setOrderState(OrderState.DELIVERING);
		OrderDetail wrongOrderDetail = TestUtils.createOrderDetail(order, book);
		wrongOrderDetail.setOrderState(OrderState.PENDING);
		orderDetails.add(wrongOrderDetail);
		when(orderDetailRepository.findByOrderId(anyLong())).thenReturn(orderDetails);
		Assertions.assertThrows(CanNotDeliveryException.class,
			() -> shippingAdminService.completeDelivery(shipping.getId()));
	}
}
