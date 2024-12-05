package shop.nuribooks.books.order.orderDetail.repository;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.publisher.entity.Publisher;
import shop.nuribooks.books.book.publisher.repository.PublisherRepository;
import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.common.config.QuerydslConfiguration;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.customer.repository.CustomerRepository;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.order.repository.OrderRepository;
import shop.nuribooks.books.order.orderdetail.dto.OrderDetailItemPageDto;
import shop.nuribooks.books.order.orderdetail.entity.OrderDetail;
import shop.nuribooks.books.order.orderdetail.repository.OrderDetailRepository;

@DataJpaTest
@Import({QuerydslConfiguration.class})
public class OrderDetailCustomRepositoryTest {
	@Autowired
	private OrderDetailRepository orderDetailRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private PublisherRepository publisherRepository;

	private Order order;
	private OrderDetail orderDetail;

	@BeforeEach
	void setUp() {
		Customer customer = TestUtils.createCustomer();
		Publisher publisher = publisherRepository.save(TestUtils.createPublisher());
		Book b00k = TestUtils.createBook(publisher);
		Order ord9r = TestUtils.createOrder(customer);
		customerRepository.save(customer);
		Book book = bookRepository.save(b00k);
		order = orderRepository.save(ord9r);
		orderDetail = orderDetailRepository.save(TestUtils.createOrderDetail(ord9r, book));
	}

	@Test
	void findAllByOrderId() {
		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrderId(order.getId());
		Assertions.assertEquals(1, orderDetails.size());
		Assertions.assertEquals(orderDetail, orderDetails.getFirst());
	}

	@Test
	void findAllByWrongOrderId() {
		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrderId(999L);
		Assertions.assertEquals(0, orderDetails.size());
	}

	@Test
	void findOrderDetail() {
		Pageable pageable = PageRequest.of(0, 3);
		OrderDetailItemPageDto orderDetailDto = orderDetailRepository.findOrderDetail(order.getId(), pageable);

		Assertions.assertEquals(1, orderDetailDto.orderDetailItem().size());
		Assertions.assertEquals(1, orderDetailDto.totalCount());
	}

	@Test
	void findOrderDetailZero() {
		Pageable pageable = PageRequest.of(0, 3);
		OrderDetailItemPageDto orderDetailDto = orderDetailRepository.findOrderDetail(999L, pageable);

		Assertions.assertEquals(0, orderDetailDto.orderDetailItem().size());
		Assertions.assertEquals(0, orderDetailDto.totalCount());
	}
}
