package shop.nuribooks.books.order.order.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.nuribooks.books.book.book.dto.BookOrderResponse;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.entity.BookStateEnum;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;
import shop.nuribooks.books.book.bookcontributor.repository.BookContributorRepository;
import shop.nuribooks.books.book.coupon.dto.MemberCouponOrderDto;
import shop.nuribooks.books.book.coupon.service.MemberCouponService;
import shop.nuribooks.books.book.point.enums.PolicyType;
import shop.nuribooks.books.book.publisher.entity.Publisher;
import shop.nuribooks.books.cart.repository.RedisCartRepository;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.exception.order.OrderNotFoundException;
import shop.nuribooks.books.member.address.dto.response.AddressResponse;
import shop.nuribooks.books.member.address.repository.AddressRepository;
import shop.nuribooks.books.member.customer.dto.CustomerDto;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.customer.repository.CustomerRepository;
import shop.nuribooks.books.member.member.dto.MemberPointDTO;
import shop.nuribooks.books.member.member.repository.MemberRepository;
import shop.nuribooks.books.order.order.dto.request.OrderRegisterRequest;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.order.repository.OrderRepository;
import shop.nuribooks.books.order.orderdetail.dto.OrderDetailRequest;
import shop.nuribooks.books.order.shipping.dto.ShippingPolicyResponse;
import shop.nuribooks.books.order.shipping.entity.ShippingPolicy;
import shop.nuribooks.books.order.shipping.repository.ShippingPolicyRepository;
import shop.nuribooks.books.order.shipping.service.ShippingService;
import shop.nuribooks.books.order.wrapping.entity.WrappingPaper;
import shop.nuribooks.books.order.wrapping.service.WrappingPaperService;

@ExtendWith(MockitoExtension.class)
class CommonOrderServiceTest {

	@Mock
	private CustomerRepository customerRepository;
	@Mock
	private BookRepository bookRepository;
	@Mock
	private AddressRepository addressRepository;
	@Mock
	private BookContributorRepository bookContributorRepository;
	@Mock
	private RedisCartRepository redisCartRepository;
	@Mock
	private ShippingPolicyRepository shippingPolicyRepository;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private OrderRepository orderRepository;
	@Mock
	private ShippingService shippingService;
	@Mock
	private WrappingPaperService wrappingPaperService;
	@Mock
	private MemberCouponService memberCouponService;

	@InjectMocks
	private CommonOrderService commonOrderService;

	private Book book;
	private Publisher publisher;

	@BeforeEach
	void setUp() {

		MockitoAnnotations.openMocks(this);

		commonOrderService = new CommonOrderService(
			customerRepository,
			bookRepository,
			addressRepository,
			bookContributorRepository,
			redisCartRepository,
			shippingPolicyRepository,
			memberRepository,
			orderRepository,
			shippingService,
			wrappingPaperService,
			memberCouponService
		);

		publisher = Publisher.builder()
			.name("누리북스")
			.build();

		book = Book.builder()
			.publisherId(publisher)
			.price(BigDecimal.valueOf(20000))
			.discountRate(10)
			.stock(100)
			.state(BookStateEnum.NORMAL)
			.thumbnailImageUrl("original_thumbnail.jpg")
			.detailImageUrl("original_detail.jpg")
			.description("Original Description")
			.contents("Original Contents")
			.isPackageable(true)
			.likeCount(0)
			.viewCount(0L)
			.build();
	}

	@Test
	@DisplayName("주문 가격 검증 성공 테스트 - 기본")
	void validateOrderPriceValidDefaultTest() {

		// given
		OrderRegisterRequest orderTempRegisterRequest = mock(OrderRegisterRequest.class);
		when(orderTempRegisterRequest.paymentPrice()).thenReturn(new BigDecimal("100"));

		OrderDetailRequest orderDetailRequest = mock(OrderDetailRequest.class);
		when(orderDetailRequest.unitPrice()).thenReturn(new BigDecimal("90"));
		when(orderDetailRequest.count()).thenReturn(1);
		when(orderTempRegisterRequest.orderDetails()).thenReturn(List.of(orderDetailRequest));

		ShippingPolicy shippingPolicy = mock(ShippingPolicy.class);
		when(shippingPolicy.getShippingFee()).thenReturn(10);
		when(shippingPolicyRepository.findClosedShippingPolicy(anyInt())).thenReturn(shippingPolicy);

		when(orderTempRegisterRequest.usingPoint()).thenReturn(BigDecimal.ZERO);
		when(orderTempRegisterRequest.wrapping()).thenReturn(null);
		when(orderTempRegisterRequest.allAppliedCoupon()).thenReturn(null);

		// when
		boolean isValid = commonOrderService.validateOrderPrice(orderTempRegisterRequest);

		// then
		assertTrue(isValid);

	}

	@Test
	@DisplayName("주문 가격 검증 성공 테스트 - 포장, 쿠폰, 포인트 포함")
	void validateOrderPriceValidWithOption1Test() {

		// given
		OrderRegisterRequest orderTempRegisterRequest = mock(OrderRegisterRequest.class);
		when(orderTempRegisterRequest.paymentPrice()).thenReturn(new BigDecimal("10000"));

		OrderDetailRequest orderDetailRequest = mock(OrderDetailRequest.class);
		when(orderDetailRequest.unitPrice()).thenReturn(new BigDecimal("10000"));
		when(orderDetailRequest.count()).thenReturn(1);
		when(orderTempRegisterRequest.orderDetails()).thenReturn(List.of(orderDetailRequest));

		ShippingPolicy shippingPolicy = mock(ShippingPolicy.class);
		when(shippingPolicy.getShippingFee()).thenReturn(1000);
		when(shippingPolicyRepository.findClosedShippingPolicy(anyInt())).thenReturn(shippingPolicy);

		when(orderTempRegisterRequest.usingPoint()).thenReturn(new BigDecimal("1000"));

		WrappingPaper wrappingPaper = mock(WrappingPaper.class);
		when(wrappingPaper.getWrappingPrice()).thenReturn(new BigDecimal("1000"));
		when(orderTempRegisterRequest.wrapping()).thenReturn(1L);
		when(wrappingPaperService.getWrappingPaper(1L)).thenReturn(wrappingPaper);

		MemberCouponOrderDto memberCouponAllType = mock(MemberCouponOrderDto.class);
		when(memberCouponAllType.discount()).thenReturn(10);
		when(memberCouponAllType.policyType()).thenReturn(PolicyType.RATED);
		when(memberCouponAllType.maximumDiscountPrice()).thenReturn(new BigDecimal("1000"));
		when(memberCouponService.getMemberCoupon(anyLong())).thenReturn(memberCouponAllType);

		// when
		boolean isValid = commonOrderService.validateOrderPrice(orderTempRegisterRequest);

		// then
		assertTrue(isValid);

	}

	@Test
	@DisplayName("주문 가격 검증 성공 테스트 - 쿠폰 최대 할인 가격 보다 작을 경우")
	void validateOrderPriceValidWithOption2Test() {

		// given
		OrderRegisterRequest orderTempRegisterRequest = mock(OrderRegisterRequest.class);
		when(orderTempRegisterRequest.paymentPrice()).thenReturn(new BigDecimal("10000"));

		OrderDetailRequest orderDetailRequest = mock(OrderDetailRequest.class);
		when(orderDetailRequest.unitPrice()).thenReturn(new BigDecimal("10000"));
		when(orderDetailRequest.count()).thenReturn(1);
		when(orderTempRegisterRequest.orderDetails()).thenReturn(List.of(orderDetailRequest));

		ShippingPolicy shippingPolicy = mock(ShippingPolicy.class);
		when(shippingPolicy.getShippingFee()).thenReturn(1000);
		when(shippingPolicyRepository.findClosedShippingPolicy(anyInt())).thenReturn(shippingPolicy);

		when(orderTempRegisterRequest.usingPoint()).thenReturn(new BigDecimal("1000"));

		WrappingPaper wrappingPaper = mock(WrappingPaper.class);
		when(wrappingPaper.getWrappingPrice()).thenReturn(new BigDecimal("1000"));
		when(orderTempRegisterRequest.wrapping()).thenReturn(1L);
		when(wrappingPaperService.getWrappingPaper(1L)).thenReturn(wrappingPaper);

		MemberCouponOrderDto memberCouponAllType = mock(MemberCouponOrderDto.class);
		when(memberCouponAllType.discount()).thenReturn(10);
		when(memberCouponAllType.policyType()).thenReturn(PolicyType.RATED);
		when(memberCouponAllType.maximumDiscountPrice()).thenReturn(new BigDecimal("2000"));
		when(memberCouponService.getMemberCoupon(anyLong())).thenReturn(memberCouponAllType);

		// when
		boolean isValid = commonOrderService.validateOrderPrice(orderTempRegisterRequest);

		// then
		assertTrue(isValid);

	}

	@Test
	@DisplayName("주문 가격 검증 실패 테스트")
	void validateOrderPriceInValidTest() {

		// given
		OrderRegisterRequest orderTempRegisterRequest = mock(OrderRegisterRequest.class);
		when(orderTempRegisterRequest.paymentPrice()).thenReturn(new BigDecimal("100"));

		OrderDetailRequest orderDetailRequest = mock(OrderDetailRequest.class);
		when(orderDetailRequest.unitPrice()).thenReturn(new BigDecimal("95"));
		when(orderDetailRequest.count()).thenReturn(1);
		when(orderTempRegisterRequest.orderDetails()).thenReturn(List.of(orderDetailRequest));

		ShippingPolicy shippingPolicy = mock(ShippingPolicy.class);
		when(shippingPolicy.getShippingFee()).thenReturn(10);
		when(shippingPolicyRepository.findClosedShippingPolicy(anyInt())).thenReturn(shippingPolicy);

		when(orderTempRegisterRequest.usingPoint()).thenReturn(BigDecimal.ZERO);
		when(orderTempRegisterRequest.wrapping()).thenReturn(null);
		when(orderTempRegisterRequest.allAppliedCoupon()).thenReturn(null);

		// when
		boolean isValid = commonOrderService.validateOrderPrice(orderTempRegisterRequest);

		// then
		assertFalse(isValid);

	}

	@Test
	@DisplayName("주문 정보 조회 성공 테스트")
	void getOrderByIdSuccessTest() {

		// given
		Order order = mock(Order.class);
		when(orderRepository.findById(anyLong())).thenReturn(Optional.ofNullable(order));

		// when
		Order resultOrder = commonOrderService.getOrderById(anyLong());

		// then
		assertEquals(order, resultOrder);
	}

	@Test
	@DisplayName("주문 정보 조회 실패 테스트")
	void getOrderByIdFailedTest() {

		// given
		Long orderId = 999L;

		// when & then
		OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> {
			commonOrderService.getOrderById(orderId);
		});

		assertEquals("해당 주문 정보가 존재하지 않습니다.", exception.getMessage());
	}

	@Test
	@DisplayName("사용자 정보 조회 성공 테스트")
	void getCustomerByIdSuccessTest() {
		// given
		Customer customer = mock(Customer.class);
		when(customerRepository.findById(anyLong())).thenReturn(Optional.ofNullable(customer));

		// when
		Customer resultCustomer = commonOrderService.getCustomerById(anyLong());

		// then
		assertEquals(customer, resultCustomer);
	}

	@Test
	@DisplayName("사용자 정보 조회 실패 테스트")
	void getCustomerByIdFailedTest() {
		// given
		Long customerId = 999L;

		// when & then
		MemberNotFoundException exception = assertThrows(MemberNotFoundException.class, () -> {
			commonOrderService.getCustomerById(customerId);
		});

		assertEquals("등록되지 않은 사용자입니다.", exception.getMessage());
	}

	@Test
	@DisplayName("사용자의 주소 목록 조회 테스트")
	void getAddressByMemberTest() {
		// given
		Customer customer = mock(Customer.class);
		when(customer.getId()).thenReturn(1L);
		when(addressRepository.findAllByMemberId(customer.getId())).thenReturn(List.of());

		// when
		List<AddressResponse> result = commonOrderService.getAddressesByMember(customer);

		// then
		assertEquals(List.of(), result);
	}

	@Test
	@DisplayName("사용자 정보 DTO 생성 테스트")
	void createCustomerDtoTest() {

		// given
		Customer customer = new Customer(1L, "정누리", "1111", "010-1234-5678", "nuri@naver.com");
		AddressResponse addressResponse = new AddressResponse(1L, "집", "Seoul", "Gangnam", "123456", false);
		List<AddressResponse> addressList = List.of(addressResponse);

		MemberPointDTO memberPoint = mock(MemberPointDTO.class);
		when(memberPoint.point()).thenReturn(new BigDecimal("100"));
		Optional<MemberPointDTO> point = Optional.of(memberPoint);

		// when
		CustomerDto resultCustmer = commonOrderService.createCustomerDto(customer, addressList, point);

		// then
		assertEquals("정누리", resultCustmer.name());
		assertEquals(new BigDecimal("100"), resultCustmer.point());

	}

	@Test
	@DisplayName("도서 정보 조회 성공 테스트")
	void getBookOrderResponseSuccessTest() {

		// given
		Long bookId = 1L;
		int quantity = 2;

		BookContributorInfoResponse contributor = new BookContributorInfoResponse(
			1L, "정누리", 1L, "작가"
		);
		List<BookContributorInfoResponse> contributors = List.of(contributor);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(bookContributorRepository.findContributorsAndRolesByBookId(bookId))
			.thenReturn(contributors);

		// when
		BookOrderResponse result = commonOrderService.getBookOrderResponses(bookId, quantity);

		// then
		assertEquals(2, result.quantity());
		assertEquals(contributors, result.contributors());
		assertEquals(new BigDecimal("36000"), result.bookTotalPrice());

	}

	@Test
	@DisplayName("도서 정보 조회 실패 테스트 - 해당 도서가 없을 때")
	void getBookOrderResponseFailedTest() {

		// given
		Long bookId = 999L;
		int quantity = 2;

		when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

		// when & then
		BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> {
			commonOrderService.getBookOrderResponses(bookId, quantity);
		});

		assertEquals("해당 도서를 찾을 수 없습니다. Id : 999", exception.getMessage());

	}

	@Test
	@DisplayName("도서 리스트 총 가격 계산 테스트")
	void calculateTotalPriceTest() {
		// given
		BookOrderResponse order1 = mock(BookOrderResponse.class);
		BookOrderResponse order2 = mock(BookOrderResponse.class);

		when(order1.bookTotalPrice()).thenReturn(BigDecimal.valueOf(100));
		when(order2.bookTotalPrice()).thenReturn(BigDecimal.valueOf(200));

		List<BookOrderResponse> orders = List.of(order1, order2);

		// when
		BigDecimal totalPrice = commonOrderService.calculateTotalPrice(orders);

		// then
		assertNotNull(totalPrice);
		assertEquals(BigDecimal.valueOf(300), totalPrice);
	}

	@Test
	@DisplayName("배송비 조회 테스트")
	void getShippingPolicyTest() {

		// given
		int orderTotalPrice = 10000;
		ShippingPolicy shippingPolicy = ShippingPolicy.builder()
			.shippingFee(3000)
			.expiration(LocalDateTime.now())
			.minimumOrderPrice(BigDecimal.ZERO)
			.build();

		when(shippingPolicyRepository.findClosedShippingPolicy(orderTotalPrice))
			.thenReturn(shippingPolicy);

		// when
		ShippingPolicyResponse result = commonOrderService.getShippingPolicy(orderTotalPrice);

		// then
		assertEquals(3000, result.shippingFee());

	}

	@Test
	@DisplayName("사용자 포인트 조회 테스트")
	void getMemberPointsTest() {
		// given
		Long memberId = 1L;

		MemberPointDTO memberPointDTO = new MemberPointDTO(new BigDecimal("100"));
		when(memberRepository.findPointById(memberId)).thenReturn(Optional.of(memberPointDTO));

		// when
		Optional<MemberPointDTO> point = commonOrderService.getMemberPoints(memberId);

		// then
		assertTrue(point.isPresent());
		assertEquals(new BigDecimal("100"), point.get().point());
	}

	@Test
	@DisplayName("사용자 포인트 조회 테스트 - 포인트가 없을 시")
	void getMemberPointTest_NoPoints() {

		// given
		Long memberId = 1L;
		when(memberRepository.findPointById(memberId)).thenReturn(Optional.empty());

		// when
		Optional<MemberPointDTO> point = commonOrderService.getMemberPoints(memberId);

		// then
		assertFalse(point.isPresent());
	}

	@Test
	@DisplayName("장바구니를 통한 책 정보 조회 테스트")
	void getBookOrderResponsesFromCartTest() {
		// given
		Map<Long, Integer> cart = new HashMap<>();
		cart.put(1L, 2);

		BookContributorInfoResponse contributor = new BookContributorInfoResponse(
			1L, "정누리", 1L, "작가"
		);

		List<BookContributorInfoResponse> contributors = List.of(contributor);

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(bookContributorRepository.findContributorsAndRolesByBookId(1L)).thenReturn(contributors);

		// when
		List<BookOrderResponse> responses = commonOrderService.getBookOrderResponsesFromCart(cart);

		// then
		assertNotNull(responses);
		assertEquals(1, responses.size());

		// verify
		verify(bookRepository, times(1)).findById(1L);
		verify(bookContributorRepository, times(1)).findContributorsAndRolesByBookId(1L);
	}

	@Test
	@DisplayName("빈 장바구니 책 조회 테스트")
	void getBookOrderResponsesFromEmptyCartTest() {
		// given
		Map<Long, Integer> cart = new HashMap<>();

		// when
		List<BookOrderResponse> responses = commonOrderService.getBookOrderResponsesFromCart(cart);

		// then
		assertNotNull(responses);
		assertTrue(responses.isEmpty());
	}

	@Test
	@DisplayName("주문 조회 테스트")
	void getOrderTest() {
		// given
		Long orderId = 1L;

		Order order = new Order(
			mock(Customer.class),
			new BigDecimal("100"),
			LocalDateTime.now(),
			new BigDecimal("100"),
			LocalDate.now(),
			null,
			new BigDecimal("100")
		);

		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

		// when
		Order resultOrder = commonOrderService.getOrder(orderId);

		// then
		assertNotNull(resultOrder);
		assertEquals(new BigDecimal("100"), resultOrder.getPaymentPrice());
	}

	@Test
	@DisplayName("주문 조회 테스트 - 찾을 수 없는 경우")
	void getOrderNotFoundTest() {
		Long orderId = 999L;

		// Mock 객체 준비
		when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

		// 예외 처리 확인
		assertThrows(OrderNotFoundException.class, () -> {
			commonOrderService.getOrder(orderId);
		});
	}

}