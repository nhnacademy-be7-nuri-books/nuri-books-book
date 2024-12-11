package shop.nuribooks.books.order.order.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import shop.nuribooks.books.order.order.dto.request.OrderListPeriodRequest;
import shop.nuribooks.books.order.order.dto.response.OrderPageResponse;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.orderdetail.repository.OrderDetailRepository;
import shop.nuribooks.books.order.shipping.entity.ShippingPolicy;
import shop.nuribooks.books.order.shipping.repository.ShippingPolicyRepository;
import shop.nuribooks.books.order.shipping.repository.ShippingRepository;
import shop.nuribooks.books.payment.payment.dto.PaymentInfoDto;

@DataJpaTest
@Import({QuerydslConfiguration.class})
class OrderCustomerRepositoryImplTest {
	@Autowired
	private OrderRepository orderCustomerRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private PublisherRepository publisherRepository;

	@Autowired
	private OrderDetailRepository orderDetailRepository;

	@Autowired
	private ShippingPolicyRepository shippingPolicyRepository;

	@Autowired
	private ShippingRepository shippingRepository;

	private Long memberId;
	private Pageable pageable;
	private OrderListPeriodRequest orderListPeriodRequest;
	private Order order;

	@BeforeEach
	void setUp() {
		System.out.println("BeforeEach executed!");
		Customer customer = TestUtils.createCustomer();
		Publisher publisher = publisherRepository.save(TestUtils.createPublisher());
		Book b00k = TestUtils.createBook(publisher);

		customerRepository.save(customer);
		Book book = bookRepository.save(b00k);
		order = orderRepository.save(TestUtils.createOrder(customer));
		orderDetailRepository.save(TestUtils.createOrderDetail(order, book));
		ShippingPolicy shippingPolicy = shippingPolicyRepository.save(TestUtils.createShippingPolicy());
		shippingRepository.save(TestUtils.createShipping(order, shippingPolicy));

		memberId = 1L;
		pageable = PageRequest.of(0, 10);
		orderListPeriodRequest =
			new OrderListPeriodRequest(LocalDate.now(), LocalDate.now().minusDays(365));
	}

	@Test
	@DisplayName("대기중인 상태를 포함한 주문 특정 유저 목록 조회")
	void findOrdersTest_pendingStatusTrue() {
		OrderPageResponse response = orderCustomerRepository.findOrders(true, memberId, pageable,
			orderListPeriodRequest);

		assertNotNull(response);
	}

	@Test
	@DisplayName("대기중인 상태를 포함하지 않은 특정 유저 주문 목록 조회")
	void findOrdersTest_pendingStatusFalse() {
		OrderPageResponse response = orderCustomerRepository.findOrders(false, memberId, pageable,
			orderListPeriodRequest);

		assertNotNull(response);
	}

	@Test
	@DisplayName("대기중인 상태를 포함하는 비회원 주문 목록 조회")
	void findNonMemberOrdersTest_pendingStatusTrue() {
		OrderPageResponse response = orderCustomerRepository.findNonMemberOrders(true, memberId, pageable,
			orderListPeriodRequest);

		assertNotNull(response);
	}

	@Test
	@DisplayName("대기중인 상태를 포함하지 않은 비회원 주문 목록 조회")
	void findNonMemberOrdersTest_pendingStatusFalse() {
		OrderPageResponse response = orderCustomerRepository.findNonMemberOrders(false, memberId, pageable,
			orderListPeriodRequest);

		assertNotNull(response);
	}

	@Test
	@DisplayName("주문 취소 목록 조회")
	void findCancelledOrdersTest() {
		OrderPageResponse response = orderCustomerRepository.findCancelledOrders(memberId, pageable,
			orderListPeriodRequest);

		assertNotNull(response);
	}

	@Test
	@DisplayName("주문 결제 정보 조회")
	void findPaymentInfoTest() {
		PaymentInfoDto response = orderCustomerRepository.findPaymentInfo(1L);
	}
}