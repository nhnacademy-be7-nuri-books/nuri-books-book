package shop.nuribooks.books.order.orderDetail.service;

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

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.order.NoStockAvailableException;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.orderdetail.dto.OrderDetailRequest;
import shop.nuribooks.books.order.orderdetail.entity.OrderDetail;
import shop.nuribooks.books.order.orderdetail.repository.OrderDetailRepository;
import shop.nuribooks.books.order.orderdetail.service.OrderDetailServiceImpl;

@ExtendWith(MockitoExtension.class)
class OrderDetailServiceImplTest {
	@InjectMocks
	OrderDetailServiceImpl orderDetailService;

	@Mock
	BookRepository bookRepository;

	@Mock
	OrderDetailRepository orderDetailRepository;

	private Book book;
	private OrderDetail orderDetail;
	private Order order;

	@BeforeEach
	void setUp() {
		book = TestUtils.createBook(TestUtils.createPublisher());
		TestUtils.setIdForEntity(book, 1L);

		order = TestUtils.createOrder(TestUtils.createCustomer());
		TestUtils.setIdForEntity(order, 1L);

		orderDetail = TestUtils.createOrderDetail(order, book);
	}

	@Test
	void registerOrderDetail_success() {
		OrderDetailRequest orderDetailRequest = new OrderDetailRequest(book.getId(), 10, BigDecimal.valueOf(10000),
			false);

		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
		when(orderDetailRepository.save(any())).thenReturn(orderDetail);

		orderDetailService.registerOrderDetail(order, orderDetailRequest);

		verify(orderDetailRepository).save(any());
	}

	@Test
	void registerOrderDetail_failed() {
		OrderDetailRequest orderDetailRequest = new OrderDetailRequest(book.getId(), 10, BigDecimal.valueOf(10000),
			false);

		when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

		Assertions.assertThrows(BookNotFoundException.class,
			() -> orderDetailService.registerOrderDetail(order, orderDetailRequest));
	}

	@Test
	void registerOrderDetail_failedStockZero() {
		OrderDetailRequest orderDetailRequest = new OrderDetailRequest(book.getId(), book.getStock() + 1,
			BigDecimal.valueOf(10000),
			false);

		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

		Assertions.assertThrows(
			NoStockAvailableException.class, () -> orderDetailService.registerOrderDetail(order, orderDetailRequest));
	}

	@Test
	void getBookTitle() {
		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

		String title = orderDetailService.getBookTitle(book.getId());
		Assertions.assertEquals(book.getTitle(), title);
	}

	@Test
	void getBookTitle_fail() {
		when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

		String title = orderDetailService.getBookTitle(book.getId());
		Assertions.assertEquals("정보 없음", title);
	}

	@Test
	void checkStock_true() {
		when(orderDetailRepository.findAllByOrderId(anyLong())).thenReturn(List.of(orderDetail));

		Assertions.assertEquals(Boolean.TRUE, orderDetailService.checkStock(order));
	}

	@Test
	void checkStock_false() {
		book.updateStock(book.getStock());
		when(orderDetailRepository.findAllByOrderId(anyLong())).thenReturn(List.of(orderDetail));

		Assertions.assertEquals(Boolean.FALSE, orderDetailService.checkStock(order));
	}
}
