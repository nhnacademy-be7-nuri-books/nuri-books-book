package shop.nuribooks.books.order.order.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.book.book.dto.BookOrderResponse;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;
import shop.nuribooks.books.book.bookcontributor.repository.BookContributorRepository;
import shop.nuribooks.books.book.point.dto.request.register.OrderUsingPointRequest;
import shop.nuribooks.books.book.point.entity.PointPolicy;
import shop.nuribooks.books.book.point.enums.PolicyName;
import shop.nuribooks.books.book.point.exception.PointPolicyNotFoundException;
import shop.nuribooks.books.book.point.repository.PointPolicyRepository;
import shop.nuribooks.books.book.point.service.PointHistoryService;
import shop.nuribooks.books.cart.entity.RedisCartKey;
import shop.nuribooks.books.cart.repository.RedisCartRepository;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.member.EmailAlreadyExistsException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.exception.member.PhoneNumberAlreadyExistsException;
import shop.nuribooks.books.exception.order.NoStockAvailableException;
import shop.nuribooks.books.exception.order.PriceMismatchException;
import shop.nuribooks.books.exception.point.PointNotFoundException;
import shop.nuribooks.books.member.address.dto.response.AddressResponse;
import shop.nuribooks.books.member.address.entity.Address;
import shop.nuribooks.books.member.address.repository.AddressRepository;
import shop.nuribooks.books.member.customer.dto.EntityMapper;
import shop.nuribooks.books.member.customer.dto.request.CustomerRegisterRequest;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.customer.repository.CustomerRepository;
import shop.nuribooks.books.member.member.dto.MemberPointDTO;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;
import shop.nuribooks.books.order.order.dto.OrderInformationResponse;
import shop.nuribooks.books.order.order.dto.OrderListPeriodRequest;
import shop.nuribooks.books.order.order.dto.OrderListResponse;
import shop.nuribooks.books.order.order.dto.OrderPageResponse;
import shop.nuribooks.books.order.order.dto.OrderTempRegisterRequest;
import shop.nuribooks.books.order.order.dto.OrderTempRegisterResponse;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.order.repository.OrderRepository;
import shop.nuribooks.books.order.orderdetail.dto.OrderDetailRequest;
import shop.nuribooks.books.order.orderdetail.service.OrderDetailService;
import shop.nuribooks.books.order.shipping.entity.ShippingPolicy;
import shop.nuribooks.books.order.shipping.repository.ShippingPolicyRepository;
import shop.nuribooks.books.order.shipping.service.ShippingService;
import shop.nuribooks.books.payment.payment.dto.PaymentRequest;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderServiceImpl implements OrderService {

	private final CustomerRepository customerRepository;
	private final MemberRepository memberRepository;
	private final OrderRepository orderRepository;
	private final BookRepository bookRepository;
	private final AddressRepository addressRepository;
	private final BookContributorRepository bookContributorRepository;
	private final ShippingPolicyRepository shippingPolicyRepository;
	private final PointPolicyRepository pointPolicyRepository;
	private final RedisCartRepository redisCartRepository;

	private final OrderDetailService orderDetailService;
	private final ShippingService shippingService;
	private final PointHistoryService pointHistoryService;

	/**
	 * 주문 폼 정보 가져오기 - 바로 주문(회원)
	 *
	 * @param id 사용자 아이디
	 * @param bookId 상품 아이디
	 * @param quantity 상품 갯수
	 * @return OrderInformationResponse
	 */
	@Override
	public OrderInformationResponse getMemberOrderInformation(Long id, Long bookId, int quantity) {

		// 사용자 확인
		Customer customer = getCustomerById(id);

		// 주소 정보 가져오기
		List<AddressResponse> addressResponseList = getAddressesByMember(customer);

		// 도서 정보 가져오기
		BookOrderResponse bookOrderResponse = getBookOrderResponses(bookId,
			quantity);
		List<BookOrderResponse> bookOrderResponses = List.of(bookOrderResponse);

		// 도서 리스트의 총 가격 반환
		BigDecimal orderTotalPrice = calculateTotalPrice(bookOrderResponses);

		// 배송비 정보 가져오기
		ShippingPolicy shippingPolicy = getShippingPolicy(orderTotalPrice.intValue());

		// 포인트 가져오기
		Optional<MemberPointDTO> point = memberRepository.findPointById(id);

		// todo: 포장

		// todo : 쿠폰

		if (point.isPresent()) {
			return OrderInformationResponse.of(
				customer,
				addressResponseList,
				bookOrderResponses,
				shippingPolicy,
				point.get().point()
			);
		} else {
			throw new PointNotFoundException();
		}
	}

	/**
	 * 주문 폼 정보 가져오기 - 바로 주문(회원)
	 *
	 * @param bookId 상품 아이디
	 * @param quantity 상품 갯수
	 * @return 주문 폼 정보
	 */
	@Override
	public OrderInformationResponse getCustomerOrderInformation(Long bookId, int quantity) {

		// 도서 정보 가져오기
		BookOrderResponse bookOrderResponse = getBookOrderResponses(bookId, quantity);
		List<BookOrderResponse> bookOrderResponses = List.of(bookOrderResponse);

		// 도서 리스트의 총 가격 반환
		BigDecimal orderTotalPrice = calculateTotalPrice(bookOrderResponses);

		// 배송비 정보 가져오기
		ShippingPolicy shippingPolicy = getShippingPolicy(orderTotalPrice.intValue());

		return OrderInformationResponse.of(bookOrderResponses, shippingPolicy);
	}

	//회원 장바구니 도서 정보 가져오기
	@Override
	public OrderInformationResponse getMemberCartOrderInformation(Long memberId) {

		Customer customer = getCustomerById(memberId);
		String cartId = RedisCartKey.MEMBER_CART.withSuffix(memberId.toString());

		// 주소 정보 가져오기
		List<AddressResponse> addressResponseList = getAddressesByMember(customer);

		// 도서 정보 가져오기
		Map<Long, Integer> cart = redisCartRepository.getCart(cartId);
		List<BookOrderResponse> bookOrderResponses = getBookOrderResponsesFromCart(cart);

		// 도서 리스트의 총 가격 반환
		BigDecimal orderTotalPrice = calculateTotalPrice(bookOrderResponses);

		// 배송비 정보 가져오기
		ShippingPolicy shippingPolicy = getShippingPolicy(orderTotalPrice.intValue());

		// 포인트 가져오기
		Optional<MemberPointDTO> point = memberRepository.findPointById(memberId);

		// todo : 쿠폰

		if (point.isPresent()) {
			return OrderInformationResponse.of(
				customer,
				addressResponseList,
				bookOrderResponses,
				shippingPolicy,
				point.get().point()
			);
		} else {
			throw new PointNotFoundException();
		}

	}

	// 비회원 장바구니 도서정보 가져오기
	@Override
	public OrderInformationResponse getCustomerCartOrderInformation(String cartId) {

		// 도서 정보 가져오기
		Map<Long, Integer> cart = redisCartRepository.getCart(cartId);
		List<BookOrderResponse> bookOrderResponses = getBookOrderResponsesFromCart(cart);

		// 도서 리스트의 총 가격 반환
		BigDecimal orderTotalPrice = calculateTotalPrice(bookOrderResponses);

		// 배송비 정보 가져오기
		ShippingPolicy shippingPolicy = getShippingPolicy(orderTotalPrice.intValue());

		return OrderInformationResponse.of(bookOrderResponses, shippingPolicy);
	}

	/**
	 * 회원 주문 임시 저장
	 *
	 * @param id UserId pk
	 * @param orderTempRegisterRequest 주문 임시 등록 request
	 * @return OrderTempRegisterResponse
	 */
	@Override
	@Transactional
	public OrderTempRegisterResponse registerTempOrderForMember(Long id,
		OrderTempRegisterRequest orderTempRegisterRequest) {

		// 사용자 확인
		Customer customer = getCustomerById(id);
		BigDecimal usedPoint = orderTempRegisterRequest.usingPoint();

		// todo : 회원 쿠폰 처리

		// 임시 주문 등록
		Order order = createOrder(customer, orderTempRegisterRequest);
		Order savedOrder = orderRepository.save(order);

		// 사용자 포인트를 사용헀다면 차감 처리
		if (usedPoint.intValue() > 0) {
			handlePointUsage(id, savedOrder, orderTempRegisterRequest.usingPoint());
		}

		// 주문 상세 등록
		List<String> bookTitles = registerOrderDetails(savedOrder, orderTempRegisterRequest.orderDetails());

		// 배송지 등록
		shippingService.registerShipping(savedOrder, orderTempRegisterRequest.shippingRegister());

		savedOrder.setTitle(makeOrderName(bookTitles));
		orderRepository.save(savedOrder);

		// 응답 반환
		return OrderTempRegisterResponse.of(savedOrder, makeOrderName(bookTitles));
	}

	/**
	 * 비회원 주문 임시 저장
	 *
	 * @param orderTempRegisterRequest 주문 임시 등록 request
	 * @return OrderTempRegisterResponse
	 */
	@Override
	@Transactional
	public OrderTempRegisterResponse registerTempOrderForCustomer(OrderTempRegisterRequest orderTempRegisterRequest) {

		// 사용자 생성
		Customer customer = registerCustomer(orderTempRegisterRequest.customerRegister());

		// 임시 주문 등록
		Order order = createOrder(customer, orderTempRegisterRequest);
		Order savedOrder = orderRepository.save(order);

		// 주문 상세 등록
		List<String> bookTitles = registerOrderDetails(savedOrder, orderTempRegisterRequest.orderDetails());

		// 배송지 등록
		shippingService.registerShipping(savedOrder, orderTempRegisterRequest.shippingRegister());

		order.setTitle(makeOrderName(bookTitles));
		orderRepository.save(savedOrder);

		// 응답 반환
		return OrderTempRegisterResponse.of(savedOrder, makeOrderName(bookTitles));
	}

	/**
	 * 주문 금액 & 재고 검증
	 *
	 * @param paymentRequest 토스 페이먼츠에서 전달받은 데이터
	 * @return 성공/실패 메시지
	 */
	@Override
	public ResponseMessage verifyOrderInformation(PaymentRequest paymentRequest) {

		String frontOrderId = paymentRequest.orderId();
		Long orderId = Long.parseLong(frontOrderId.substring(frontOrderId.length() - 2));

		Optional<Order> order = orderRepository.findById(orderId);

		if (order.isPresent()) {
			if (!orderDetailService.checkStock(order.get())) {
				throw new NoStockAvailableException();
			}
			if (order.get().getPaymentPrice().intValue() != paymentRequest.amount()) {
				throw new PriceMismatchException();
			}
		}

		log.debug("재고 최종 검증 성공");

		return ResponseMessage.builder()
			.statusCode(200)
			.message("토스 페이먼츠 검증 완료")
			.build();
	}

	@Override
	public Page<OrderListResponse> getOrderList(
		boolean includeOrdersInPendingStatus,
		Pageable pageable,
		OrderListPeriodRequest orderListPeriodRequest,
		Optional<Long> userId) {

		OrderPageResponse result = null;
		if (userId.isPresent()) {
			result = orderRepository.findOrders(includeOrdersInPendingStatus, userId.get(),
				pageable, orderListPeriodRequest);
		}

		Page<OrderListResponse> response =
			new PageImpl(result.orders(), pageable, result.totalCount());

		return response;
	}

	// -------------- private

	/**
	 * 주문 상세 등록
	 *
	 * @param savedOrder 저장된 주문 정보
	 * @param orderDetails 주문 상세
	 * @return 주문 상세 목록 제목
	 */
	private List<String> registerOrderDetails(Order savedOrder, List<OrderDetailRequest> orderDetails) {
		List<String> bookTitles = new ArrayList<>();
		for (OrderDetailRequest detail : orderDetails) {
			orderDetailService.registerOrderDetail(savedOrder, detail);
			bookTitles.add(orderDetailService.getBookTitle(detail.bookId()));
		}
		return bookTitles;
	}

	/**
	 * 주문 명 생성
	 *
	 * <p>
	 *     책 종류가 1권일 경우 : 000 1건
	 *     책 종류가 n권일 경우 : 000 외 n-1건
	 * </p>
	 *
	 * @param bookTitles 주문 도서 타이틀 목록
	 * @return 주문 도서를 이용해서 생성한 주문 명
	 */
	private String makeOrderName(List<String> bookTitles) {

		if (Objects.isNull(bookTitles)) {
			return "";
		}

		if (bookTitles.size() == 1) {
			return bookTitles.getFirst() + " 1건";
		} else {
			String firstBook = bookTitles.getFirst();
			int bookListCount = bookTitles.size() - 1;
			return firstBook + " 외 " + bookListCount + "건";
		}
	}

	/**
	 * 사용자 확인
	 *
	 * @param id 사용자 아이디
	 * @return Customer
	 */
	private Customer getCustomerById(Long id) {
		return customerRepository.findById(id)
			.orElseThrow(() -> new MemberNotFoundException("등록되지 않은 사용자입니다."));
	}

	/**
	 * 사용자로 부터 주소 목록 가져오기
	 *
	 * @param customer 사용자
	 * @return 주소 목록
	 */
	private List<AddressResponse> getAddressesByMember(Customer customer) {
		List<Address> addressesByMemberId = addressRepository.findAllByMemberId(customer.getId());
		return addressesByMemberId.stream().map(AddressResponse::of).toList();
	}

	/**
	 * 장바구니를 통한 책 정보 가져오기
	 *
	 * @param cart 장바구니
	 * @return 책 목록
	 */
	private List<BookOrderResponse> getBookOrderResponsesFromCart(Map<Long, Integer> cart) {
		return new ArrayList<>(cart.entrySet().stream()
			.map(entry -> getBookOrderResponses(entry.getKey(), entry.getValue()))
			.toList());
	}

	/**
	 * 도서 정보 가져오기
	 *
	 * @param bookId 책 아이디
	 * @param quantity 수량
	 * @return BookOrderResponse
	 */
	private BookOrderResponse getBookOrderResponses(Long bookId, int quantity) {
		// 도서 정보 가져오기
		Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId));

		// 도서 기여자 정보 가져오기
		List<BookContributorInfoResponse> contributors = bookContributorRepository.findContributorsAndRolesByBookId(
			bookId);

		// 만족하는 객체 생성
		return BookOrderResponse.of(book, contributors, quantity);
	}

	/**
	 * 도서 리스트의 총 가격 계산
	 *
	 * @param bookOrderResponses List<BookOrderResponse>
	 * @return BigDecimal
	 */
	private BigDecimal calculateTotalPrice(List<BookOrderResponse> bookOrderResponses) {
		BigDecimal totalPrice = BigDecimal.ZERO;
		for (BookOrderResponse bookOrder : bookOrderResponses) {
			totalPrice = totalPrice.add(bookOrder.bookTotalPrice());
		}
		return totalPrice;
	}

	/**
	 * 	배송비 정책 조회
	 */
	private ShippingPolicy getShippingPolicy(int orderTotalPrice) {
		return shippingPolicyRepository.findClosedShippingPolicy(orderTotalPrice);
	}

	/**
	 * 비회원 저장 <br>
	 * 고객 생성 후 customerRepository에 저장
	 * @throws EmailAlreadyExistsException 이미 존재하는 이메일입니다.
	 * @param request
	 * CustomerCreateRequest로 이름, 비밀번호, 전화번호, 이메일을 받는다. <br>
	 * 이메일로 비회원 조회 후 이미 존재하는 이메일이면 예외 던짐
	 * @return customer 등록 후 입력한 이름, 전화번호, 이메일을 그대로 CustomerCreateResponse에 담아서 반환
	 */
	public Customer registerCustomer(CustomerRegisterRequest request) {
		if (customerRepository.existsByEmail(request.email())) {
			throw new EmailAlreadyExistsException("이미 존재하는 이메일입니다.");
		}
		if (customerRepository.existsByPhoneNumber(request.phoneNumber())) {
			throw new PhoneNumberAlreadyExistsException("이미 존재하는 전화번호입니다.");
		}

		Customer customer = EntityMapper.toCustomerEntity(request);

		return customerRepository.save(customer);
	}

	/**
	 * 주문 등록
	 *
	 * @param customer 사용자
	 * @param orderTempRegisterRequest 주문 등록 정보
	 * @return 주문
	 */
	private Order createOrder(Customer customer, OrderTempRegisterRequest orderTempRegisterRequest) {
		return Order.builder()
			.customer(customer)
			.paymentPrice(orderTempRegisterRequest.paymentPrice())
			.orderedAt(LocalDateTime.now())
			.wrappingPrice(orderTempRegisterRequest.wrappingPrice())
			.expectedDeliveryAt(orderTempRegisterRequest.expectedDeliveryAt())
			.build();
	}

	/**
	 * 포인트 차감 처리
	 * @param id 사용자 아이디
	 * @param savedOrder 저장된 주문
	 * @param usedPoint 사용한 포인트
	 */
	private void handlePointUsage(Long id, Order savedOrder, BigDecimal usedPoint) {
		Optional<Member> member = memberRepository.findById(id);

		member.ifPresent(value -> {
			Optional<PointPolicy> pointPolicy = pointPolicyRepository.findPointPolicyByNameIgnoreCaseAndDeletedAtIsNull(
				PolicyName.USING.toString());

			if (pointPolicy.isEmpty()) {
				throw new PointPolicyNotFoundException();
			}

			OrderUsingPointRequest orderUsingPointRequest = new OrderUsingPointRequest(
				member.get(), savedOrder, usedPoint);

			this.pointHistoryService.registerPointHistory(orderUsingPointRequest, PolicyName.USING);
		});
	}
}
