package shop.nuribooks.books.order.order.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
import shop.nuribooks.books.book.point.repository.PointHistoryRepository;
import shop.nuribooks.books.book.point.repository.PointPolicyRepository;
import shop.nuribooks.books.book.point.service.PointHistoryService;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.member.EmailAlreadyExistsException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.exception.member.PhoneNumberAlreadyExistsException;
import shop.nuribooks.books.exception.order.NoStockAvailableException;
import shop.nuribooks.books.exception.order.PriceMismatchException;
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
import shop.nuribooks.books.order.order.dto.OrderTempRegisterRequest;
import shop.nuribooks.books.order.order.dto.OrderTempRegisterResponse;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.order.repository.OrderRepository;
import shop.nuribooks.books.order.orderDetail.dto.OrderDetailRequest;
import shop.nuribooks.books.order.orderDetail.service.OrderDetailService;
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

	private final OrderDetailService orderDetailService;
	private final ShippingService shippingService;
	private final PointHistoryRepository pointHistoryRepository;
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
		List<Address> addressesByMemberId = addressRepository.findAllByMemberId(customer.getId());
		List<AddressResponse> addressResponseList = addressesByMemberId.stream()
			.map(AddressResponse::of)
			.toList();

		// 도서 정보 가져오기
		List<BookOrderResponse> bookOrderResponses = getBookOrderResponses(bookId,
			quantity);

		// 도서 리스트의 총 가격 반환
		BigDecimal orderTotalPrice = calculateTotalPrice(bookOrderResponses);

		// 배송비 정보 가져오기
		ShippingPolicy shippingPolicy = getShippingPolicy(orderTotalPrice.intValue());

		// 포인트 가져오기
		Optional<MemberPointDTO> point = memberRepository.findPointById(id);

		// todo : 쿠폰

		return OrderInformationResponse.of(
			customer,
			addressResponseList,
			bookOrderResponses,
			shippingPolicy,
			point.get().point());
	}

	/**
	 * 주문 폼 정보 가져오기 - 바로 주문(회원)
	 *
	 * @param bookId 상품 아이디
	 * @param quantity 상품 갯수
	 * @return
	 */
	@Override
	public OrderInformationResponse getCustomerOrderInformation(Long bookId, int quantity) {

		// 도서 정보 가져오기
		List<BookOrderResponse> bookOrderResponses = getBookOrderResponses(bookId,
			quantity);

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
		Order order = Order.builder()
			.customer(customer)
			.paymentPrice(orderTempRegisterRequest.paymentPrice())
			.orderedAt(LocalDateTime.now())
			.wrappingPrice(orderTempRegisterRequest.wrappingPrice())
			.expectedDeliveryAt(orderTempRegisterRequest.expectedDeliveryAt())
			.build();

		Order savedOrder = orderRepository.save(order);

		// 사용자 포인트를 사용헀다면 차감 처리
		if (usedPoint.intValue() > 0) {
			Optional<Member> member = memberRepository.findById(id);

			member.ifPresent(value -> {

				// 포인트 불러오기
				Optional<PointPolicy> pointPolicy = pointPolicyRepository.findPointPolicyByNameIgnoreCaseAndDeletedAtIsNull(
					PolicyName.USING.toString());

				OrderUsingPointRequest orderUsingPointRequest = new OrderUsingPointRequest(
					member.get(),
					savedOrder,
					orderTempRegisterRequest.usingPoint()
				);

				// 포인트 차감, 내역 저장
				this.pointHistoryService.registerPointHistory(orderUsingPointRequest, PolicyName.USING);

			});

		}

		// 주문 상세 등록
		List<String> bookTitles = new ArrayList<>();
		for (OrderDetailRequest detail : orderTempRegisterRequest.orderDetails()) {
			orderDetailService.registerOrderDetail(order, detail);

			// 책 제목 추출
			bookTitles.add(orderDetailService.getBookTitle(detail.bookId()));

		}

		// 배송지 등록
		shippingService.registerShipping(order, orderTempRegisterRequest.shippingRegister());

		order.setTitle(makeOrderName(bookTitles));
		orderRepository.save(order);

		// 응답 반환
		return OrderTempRegisterResponse.of(order, makeOrderName(bookTitles));
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
		Order order = Order.builder()
			.customer(customer)
			.paymentPrice(orderTempRegisterRequest.paymentPrice())
			.orderedAt(LocalDateTime.now())
			.wrappingPrice(orderTempRegisterRequest.wrappingPrice())
			.expectedDeliveryAt(orderTempRegisterRequest.expectedDeliveryAt())
			.build();

		orderRepository.save(order);

		// 주문 상세 등록
		List<String> bookTitles = new ArrayList<>();
		for (OrderDetailRequest detail : orderTempRegisterRequest.orderDetails()) {
			orderDetailService.registerOrderDetail(order, detail);
			// 책 제목 추출
			bookTitles.add(orderDetailService.getBookTitle(detail.bookId()));
		}

		// 배송지 등록
		shippingService.registerShipping(order, orderTempRegisterRequest.shippingRegister());

		order.setTitle(makeOrderName(bookTitles));
		orderRepository.save(order);

		// 응답 반환
		return OrderTempRegisterResponse.of(order, makeOrderName(bookTitles));
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
	 * @param id 사용자 아이디
	 * @return Customer
	 */
	private Customer getCustomerById(Long id) {
		return customerRepository.findById(id)
			.orElseThrow(() -> new MemberNotFoundException("등록되지 않은 사용자입니다."));
	}

	/**
	 * 도서 정보 가져오기
	 *
	 * @param bookId 책 아이디
	 * @param quantity 수량
	 * @return BookOrderResponse
	 */
	private List<BookOrderResponse> getBookOrderResponses(Long bookId, int quantity) {
		// 도서 정보 가져오기
		Optional<Book> book = Optional.ofNullable(bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException(bookId)));

		// 도서 기여자 정보 가져오기
		List<BookContributorInfoResponse> contributors = bookContributorRepository.findContributorsAndRolesByBookId(
			bookId);

		// 만족하는 객체 생성
		List<BookOrderResponse> bookOrderResponses = new ArrayList<>();
		BookOrderResponse bookOrderResponse = BookOrderResponse.of(book.get(), contributors, quantity);
		bookOrderResponses.add(bookOrderResponse);
		return bookOrderResponses;
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
}
