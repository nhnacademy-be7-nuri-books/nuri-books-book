package shop.nuribooks.books.order.order.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.book.book.dto.BookOrderResponse;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.bookcontributor.repository.BookContributorRepository;
import shop.nuribooks.books.book.coupon.dto.CouponAppliedOrderDto;
import shop.nuribooks.books.book.coupon.dto.MemberCouponOrderDto;
import shop.nuribooks.books.book.coupon.entity.AllAppliedCoupon;
import shop.nuribooks.books.book.coupon.entity.MemberCoupon;
import shop.nuribooks.books.book.coupon.repository.AllAppliedCouponRepository;
import shop.nuribooks.books.book.coupon.repository.MemberCouponRepository;
import shop.nuribooks.books.book.coupon.service.MemberCouponService;
import shop.nuribooks.books.book.point.entity.PointHistory;
import shop.nuribooks.books.book.point.entity.child.OrderSavingPoint;
import shop.nuribooks.books.book.point.entity.child.OrderUsingPoint;
import shop.nuribooks.books.book.point.enums.PolicyType;
import shop.nuribooks.books.book.point.repository.OrderSavingPointRepository;
import shop.nuribooks.books.book.point.repository.OrderUsingPointRepository;
import shop.nuribooks.books.book.point.repository.PointHistoryRepository;
import shop.nuribooks.books.cart.entity.RedisCartKey;
import shop.nuribooks.books.cart.repository.RedisCartRepository;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.exception.member.EmailAlreadyExistsException;
import shop.nuribooks.books.exception.member.MemberCartNotFoundException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.exception.member.PhoneNumberAlreadyExistsException;
import shop.nuribooks.books.exception.order.NoStockAvailableException;
import shop.nuribooks.books.exception.order.OrderNotFoundException;
import shop.nuribooks.books.exception.order.OrderPriceValidationFailException;
import shop.nuribooks.books.exception.order.PriceMismatchException;
import shop.nuribooks.books.exception.order.detail.OrderNotBelongsToUserException;
import shop.nuribooks.books.exception.point.PointNotFoundException;
import shop.nuribooks.books.member.address.dto.response.AddressResponse;
import shop.nuribooks.books.member.address.repository.AddressRepository;
import shop.nuribooks.books.member.customer.dto.CustomerDto;
import shop.nuribooks.books.member.customer.dto.EntityMapper;
import shop.nuribooks.books.member.customer.dto.request.CustomerRegisterRequest;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.customer.repository.CustomerRepository;
import shop.nuribooks.books.member.member.dto.MemberPointDTO;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;
import shop.nuribooks.books.order.order.dto.OrderCancelDto;
import shop.nuribooks.books.order.order.dto.OrderSummaryDto;
import shop.nuribooks.books.order.order.dto.request.OrderCancelRequest;
import shop.nuribooks.books.order.order.dto.request.OrderListPeriodRequest;
import shop.nuribooks.books.order.order.dto.request.OrderRegisterRequest;
import shop.nuribooks.books.order.order.dto.response.OrderInformationResponse;
import shop.nuribooks.books.order.order.dto.response.OrderListResponse;
import shop.nuribooks.books.order.order.dto.response.OrderPageResponse;
import shop.nuribooks.books.order.order.dto.response.OrderRegisterResponse;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.order.event.OrderCancelPointEvent;
import shop.nuribooks.books.order.order.event.PointUsedEvent;
import shop.nuribooks.books.order.order.repository.OrderRepository;
import shop.nuribooks.books.order.orderdetail.dto.OrderDetailItemDto;
import shop.nuribooks.books.order.orderdetail.dto.OrderDetailItemPageDto;
import shop.nuribooks.books.order.orderdetail.dto.OrderDetailRequest;
import shop.nuribooks.books.order.orderdetail.dto.OrderDetailResponse;
import shop.nuribooks.books.order.orderdetail.entity.OrderDetail;
import shop.nuribooks.books.order.orderdetail.entity.OrderState;
import shop.nuribooks.books.order.orderdetail.repository.OrderDetailRepository;
import shop.nuribooks.books.order.orderdetail.service.OrderDetailService;
import shop.nuribooks.books.order.shipping.dto.ShippingInfoDto;
import shop.nuribooks.books.order.shipping.dto.ShippingPolicyResponse;
import shop.nuribooks.books.order.shipping.entity.Shipping;
import shop.nuribooks.books.order.shipping.repository.ShippingPolicyRepository;
import shop.nuribooks.books.order.shipping.repository.ShippingRepository;
import shop.nuribooks.books.order.shipping.service.ShippingService;
import shop.nuribooks.books.order.wrapping.dto.WrappingPaperResponse;
import shop.nuribooks.books.order.wrapping.entity.WrappingPaper;
import shop.nuribooks.books.order.wrapping.service.WrappingPaperService;
import shop.nuribooks.books.payment.payment.dto.PaymentInfoDto;
import shop.nuribooks.books.payment.payment.dto.PaymentRequest;
import shop.nuribooks.books.payment.payment.service.PaymentService;

@Service
@Transactional(readOnly = true)
@Slf4j
public class OrderServiceImpl extends AbstractOrderService implements OrderService {

	private final OrderDetailRepository orderDetailRepository;
	private final ShippingRepository shippingRepository;
	private final MemberCouponRepository memberCouponRepository;
	private final AllAppliedCouponRepository allAppliedCouponRepository;
	private final OrderUsingPointRepository orderUsingPointRepository;
	private final PointHistoryRepository pointHistoryRepository;

	private final OrderDetailService orderDetailService;
	private final ApplicationEventPublisher publisher;
	private final PaymentService paymentService;
	private final OrderSavingPointRepository orderSavingPointRepository;

	public OrderServiceImpl(CustomerRepository customerRepository,
		BookRepository bookRepository,
		AddressRepository addressRepository,
		BookContributorRepository bookContributorRepository,
		RedisCartRepository redisCartRepository,
		ShippingPolicyRepository shippingPolicyRepository,
		MemberRepository memberRepository,
		OrderRepository orderRepository,
		OrderDetailRepository orderDetailRepository,
		ShippingRepository shippingRepository,
		ShippingService shippingService,
		MemberCouponRepository memberCouponRepository,
		OrderDetailService orderDetailService,
		WrappingPaperService wrappingPaperService,
		MemberCouponService memberCouponService,
		AllAppliedCouponRepository allAppliedCouponRepository,
		OrderUsingPointRepository orderUsingPointRepository, PointHistoryRepository pointHistoryRepository,
		ApplicationEventPublisher publisher,
		PaymentService paymentService,
		OrderSavingPointRepository orderSavingPointRepository) {
		super(customerRepository,
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
		this.orderDetailRepository = orderDetailRepository;
		this.shippingRepository = shippingRepository;
		this.memberCouponRepository = memberCouponRepository;
		this.orderDetailService = orderDetailService;
		this.allAppliedCouponRepository = allAppliedCouponRepository;
		this.orderUsingPointRepository = orderUsingPointRepository;
		this.pointHistoryRepository = pointHistoryRepository;
		this.publisher = publisher;
		this.paymentService = paymentService;
		this.orderSavingPointRepository = orderSavingPointRepository;
	}

	/**
	 * 주문 폼 정보 가져오기 - 바로 주문(회원)
	 *
	 * @param memberId 사용자 아이디
	 * @param bookId 상품 아이디
	 * @param quantity 상품 갯수
	 * @return OrderInformationResponse
	 */
	@Override
	public OrderInformationResponse getMemberOrderInformation(Long memberId, Long bookId, int quantity
	) {

		// 사용자 정보
		Customer customer = getCustomerById(memberId);
		List<AddressResponse> addressResponseList = getAddressesByMember(customer);
		Optional<MemberPointDTO> point = getMemberPoints(memberId);
		CustomerDto customerDto = createCustomerDto(customer, addressResponseList, point);

		// 도서 정보 가져오기
		BookOrderResponse bookOrderResponse = getBookOrderResponses(bookId,
			quantity);
		List<BookOrderResponse> bookOrderResponses = List.of(bookOrderResponse);

		// 도서 리스트의 총 가격 반환
		BigDecimal orderTotalPrice = calculateTotalPrice(bookOrderResponses);

		// 배송비 정보 가져오기
		ShippingPolicyResponse shippingPolicy = getShippingPolicy(orderTotalPrice.intValue());

		// 포장지 목록 가져오기
		List<WrappingPaperResponse> paperResponse = wrappingPaperService.getAllWrappingPaper();

		// 쿠폰 목록 가져오기 (ALL 타입)
		List<MemberCouponOrderDto> allTypeCoupon = memberCouponService.getAllTypeAvailableCouponsByMemberId(memberId,
			orderTotalPrice);

		if (point.isPresent()) {
			return OrderInformationResponse.builder()
				.customer(customerDto)
				.bookOrderResponse(bookOrderResponses)
				.shippingPolicyResponse(shippingPolicy)
				.paperResponse(paperResponse)
				.allTypeCoupon(allTypeCoupon)
				.build();
		} else {
			throw new PointNotFoundException();
		}
	}

	/**
	 * 주문 폼 정보 가져오기 - 바로 주문(비회원)
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
		ShippingPolicyResponse shippingPolicy = getShippingPolicy(orderTotalPrice.intValue());

		// 포장지 목록 가져오기
		List<WrappingPaperResponse> paperResponse = wrappingPaperService.getAllWrappingPaper();

		return OrderInformationResponse.builder()
			.bookOrderResponse(bookOrderResponses)
			.shippingPolicyResponse(shippingPolicy)
			.paperResponse(paperResponse)
			.build();
	}

	/**
	 * 회원 장바구니 도서 정보 가져오기
	 *
	 * @param memberId 회원 아이디
	 * @return 주문 폼 정보
	 */
	@Override
	public OrderInformationResponse getMemberCartOrderInformation(Long memberId) {

		String cartId = RedisCartKey.MEMBER_CART.withSuffix(memberId.toString());

		// 사용자 정보
		Customer customer = getCustomerById(memberId);
		List<AddressResponse> addressResponseList = getAddressesByMember(customer);
		Optional<MemberPointDTO> point = getMemberPoints(memberId);
		CustomerDto customerDto = createCustomerDto(customer, addressResponseList, point);

		// 도서 정보 가져오기
		Map<Long, Integer> cart = redisCartRepository.getCart(cartId);
		List<BookOrderResponse> bookOrderResponses = getBookOrderResponsesFromCart(cart);

		// 도서 리스트의 총 가격 반환
		BigDecimal orderTotalPrice = calculateTotalPrice(bookOrderResponses);

		// 배송비 정보 가져오기
		ShippingPolicyResponse shippingPolicy = getShippingPolicy(orderTotalPrice.intValue());

		// 포장지 목록 가져오기
		List<WrappingPaperResponse> paperResponse = wrappingPaperService.getAllWrappingPaper();

		// 쿠폰 목록 가져오기 (ALL 타입)
		List<MemberCouponOrderDto> allTypeCoupon = memberCouponService.getAllTypeAvailableCouponsByMemberId(memberId,
			orderTotalPrice);

		if (point.isPresent()) {
			return OrderInformationResponse.builder()
				.customer(customerDto)
				.bookOrderResponse(bookOrderResponses)
				.shippingPolicyResponse(shippingPolicy)
				.paperResponse(paperResponse)
				.allTypeCoupon(allTypeCoupon)
				.build();
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
		ShippingPolicyResponse shippingPolicy = getShippingPolicy(orderTotalPrice.intValue());

		// 포장지 목록 가져오기
		List<WrappingPaperResponse> paperResponse = wrappingPaperService.getAllWrappingPaper();

		return OrderInformationResponse.builder()
			.bookOrderResponse(bookOrderResponses)
			.shippingPolicyResponse(shippingPolicy)
			.paperResponse(paperResponse)
			.build();
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
	public OrderRegisterResponse registerTempOrderForMember(Long id,
		OrderRegisterRequest orderTempRegisterRequest) {

		// 사용자 확인
		Customer customer = getCustomerById(id);
		BigDecimal usedPoint = orderTempRegisterRequest.usingPoint();

		// 임시 주문 등록
		Order order = createOrder(customer, orderTempRegisterRequest);
		Order savedOrder = orderRepository.save(order);

		// 사용자 포인트를 사용헀다면 차감 처리
		if (usedPoint.intValue() > 0) {
			handlePointUsage(id, savedOrder, orderTempRegisterRequest.usingPoint());
		}

		List<String> bookTitles;

		// 포장 처리
		if (Objects.nonNull(orderTempRegisterRequest.wrapping())) {
			List<OrderDetailRequest> updatedOrderDetails;
			WrappingPaper wrappingPaper = wrappingPaperService.getWrappingPaper(
				orderTempRegisterRequest.wrapping());
			savedOrder.setWrappingPaper(wrappingPaper);

			updatedOrderDetails = processWrapping(orderTempRegisterRequest.orderDetails(),
				orderTempRegisterRequest.wrappingList());

			// 주문 상세 등록
			bookTitles = registerOrderDetails(savedOrder, updatedOrderDetails);
		} else {
			// 주문 상세 등록
			bookTitles = registerOrderDetails(savedOrder, orderTempRegisterRequest.orderDetails());
		}

		// 배송지 등록
		shippingService.registerShipping(savedOrder, orderTempRegisterRequest.shippingRegister());

		// 회원 쿠폰 처리

		// 주문 전체 적용 쿠폰
		if (Objects.nonNull(orderTempRegisterRequest.allAppliedCoupon())) {
			processCoupon(orderTempRegisterRequest, savedOrder);
		}

		// 주문 update
		savedOrder.setTitle(makeOrderName(bookTitles));
		orderRepository.save(savedOrder);

		// 가격 검증 처리
		if (!validateOrderPrice(orderTempRegisterRequest)) {
			log.error("클라이언트에서 넘겨받은 결제 금액과 실제 결제 금액이 다릅니다.");
			throw new OrderPriceValidationFailException();
		}

		log.debug("주문 저장 성공");

		// 응답 반환
		return OrderRegisterResponse.of(savedOrder, makeOrderName(bookTitles));
	}

	/**
	 * 비회원 주문 임시 저장
	 *
	 * @param orderTempRegisterRequest 주문 임시 등록 request
	 * @return OrderTempRegisterResponse
	 */
	@Override
	@Transactional
	public OrderRegisterResponse registerTempOrderForCustomer(OrderRegisterRequest orderTempRegisterRequest) {

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
		return OrderRegisterResponse.of(savedOrder, makeOrderName(bookTitles));
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
		Long orderId = Long.parseLong(frontOrderId.substring(frontOrderId.length() - 4));

		Order order = getOrderById(orderId);

		if (!orderDetailService.checkStock(order)) {
			log.error("결제 중 재고 없음");
			throw new NoStockAvailableException("결제 중");
		}

		BigDecimal paymentPrice = new BigDecimal(paymentRequest.amount());
		if (order.getPaymentPrice().compareTo(paymentPrice) != 0) {
			log.error("클라이언트에서 결제하려는 금액이 서버의 금액과 일치하지 않음");
			throw new PriceMismatchException();
		}

		log.debug("재고 최종 검증 성공");

		return ResponseMessage.builder()
			.statusCode(200)
			.message("토스 페이먼츠 검증 완료")
			.build();
	}

	/**
	 * 주문 목록 가져오기
	 *
	 * @param includeOrdersInPendingStatus 대기 미포함 여부
	 * @param pageable 페이지
	 * @param orderListPeriodRequest 적용 날짜
	 * @param memberId 사용자 아이디
	 * @return 주문 목록
	 */
	@Override
	public Page<OrderListResponse> getOrderList(
		boolean includeOrdersInPendingStatus,
		Pageable pageable,
		OrderListPeriodRequest orderListPeriodRequest,
		Optional<Long> memberId) {

		OrderPageResponse result = null;
		if (memberId.isPresent()) {
			result = orderRepository.findOrders(includeOrdersInPendingStatus, memberId.get(),
				pageable, orderListPeriodRequest);
		}

		return new PageImpl<>(result.orders(), pageable, result.totalCount());
	}

	@Override
	public Page<OrderListResponse> getNonMemberOrderList(boolean includeOrdersInPendingStatus, Pageable pageable,
		OrderListPeriodRequest orderListPeriodRequest, Optional<Long> customerId) {
		OrderPageResponse result = null;
		if (customerId.isPresent()) {
			result = orderRepository.findNonMemberOrders(includeOrdersInPendingStatus, customerId.get(),
				pageable, orderListPeriodRequest);
		}

		return new PageImpl<>(result.orders(), pageable, result.totalCount());
	}

	/**
	 * 주문 취소/환불 목록 가져오기
	 *
	 * @param pageable 페이지
	 * @param orderListPeriodRequest 적용 날짜
	 * @param memberId 사용자 아이디
	 * @return 주문 취소/환불 목록
	 */
	@Override
	public Page<OrderListResponse> getCancelledOrderList(Pageable pageable,
		OrderListPeriodRequest orderListPeriodRequest, Optional<Long> memberId) {

		OrderPageResponse result = null;
		if (memberId.isPresent()) {
			result = orderRepository.findCancelledOrders(memberId.get(),
				pageable, orderListPeriodRequest);
		}

		return new PageImpl<>(result.orders(), pageable, result.totalCount());
	}

	/**
	 * 주문 취소 폼 불러오기
	 * @param memberId 사용자 아이디
	 * @param orderId 주문 아이디
	 * @return 주문 취소 폼
	 */
	@Override
	public OrderCancelDto getOrderCancel(Optional<Long> memberId, Long orderId) {

		Order order = getOrderById(orderId);

		// 주문 쿠폰
		Optional<AllAppliedCoupon> allAppliedCoupon = allAppliedCouponRepository.findByOrder(order);
		List<CouponAppliedOrderDto> bookAppliedCouponList = new ArrayList<>();
		if (allAppliedCoupon.isPresent()) {
			Optional<MemberCoupon> memberCoupon = memberCouponRepository.findById(allAppliedCoupon.get().getId());
			if (memberCoupon.isPresent()) {
				CouponAppliedOrderDto couponAppliedOrderDto = CouponAppliedOrderDto.builder()
					.name(memberCoupon.get().getCoupon().getName())
					.discountPrice(allAppliedCoupon.get().getDiscount_price())
					.couponType(memberCoupon.get().getCoupon().getCouponType())
					.build();

				bookAppliedCouponList.add(couponAppliedOrderDto);
			}
		}

		// 도서 쿠폰

		// 포인트
		BigDecimal appliedPoint = BigDecimal.ZERO;
		Optional<OrderUsingPoint> orderUsingPoint = orderUsingPointRepository.findByOrder(order);

		if (orderUsingPoint.isPresent()) {
			appliedPoint = orderUsingPoint.get().getAmount();
		}

		return OrderCancelDto.builder()
			.paymentPrice(order.getPaymentPrice())
			.bookAppliedCouponList(bookAppliedCouponList)
			.usingPoint(appliedPoint)
			.build();
	}

	/**
	 * 주문 상세 가져오기
	 * @param userId 사용자 아이디
	 * @param orderId 주문 아이디
	 * @param pageable 페이징
	 * @return 주문 상세 정보
	 */
	@Override
	public OrderDetailResponse getOrderDetail(Optional<Long> userId, Long orderId, Pageable pageable) {

		Order order = getOrder(orderId);

		if (!Objects.equals(order.getCustomer().getId(), userId.get())) {
			log.error("주문 상세 조회 - {} 가 소유한 주문이 아님", userId.get());
			throw new OrderNotBelongsToUserException("해당 주문 정보의 소유자가 아닙니다.");
		}

		WrappingPaper wrappingPaper = order.getWrappingPaper();
		WrappingPaperResponse wrappingPaperResponse = null;

		if (Objects.nonNull(wrappingPaper)) {
			wrappingPaperResponse = WrappingPaperResponse.builder()
				.id(wrappingPaper.getId())
				.title(wrappingPaper.getTitle())
				.imageUrl(wrappingPaper.getImageUrl())
				.wrappingPrice(wrappingPaper.getWrappingPrice())
				.build();
		}

		// 주문 요약 정보
		OrderSummaryDto orderSummaryDto = OrderSummaryDto.builder()
			.title(order.getTitle())
			.orderedAt(order.getOrderedAt())
			.wrappingInfo(wrappingPaperResponse)
			.build();

		// 배송 정보
		Shipping shipping = shippingRepository.findByOrder(order);

		ShippingInfoDto shippingInfoDto = ShippingInfoDto.builder()
			.recipientName(shipping.getRecipientName())
			.recipientPhoneNumber(shipping.getRecipientPhoneNumber())
			.recipientAddress(shipping.getRecipientAddress())
			.recipientAddressDetail(shipping.getRecipientAddressDetail())
			.recipientZipcode(shipping.getRecipientZipcode())
			.build();

		// 주문 항목
		OrderDetailItemPageDto orderDetailItem = orderDetailRepository.findOrderDetail(orderId, pageable);

		Page<OrderDetailItemDto> orderListResponses =
			new PageImpl<>(orderDetailItem.orderDetailItem(), pageable, orderDetailItem.totalCount());

		// 결제 항목
		PaymentInfoDto paymentInfoDto = orderRepository.findPaymentInfo(orderId);

		return OrderDetailResponse.builder()
			.order(orderSummaryDto)
			.orderItems(orderListResponses)
			.shipping(shippingInfoDto)
			.payment(paymentInfoDto).build();
	}

	@Override
	public OrderDetailResponse getNonMemberOrderDetail(Optional<Long> customerId, Long orderId,
		Pageable pageable) {

		Order order = getOrder(orderId);

		if (!Objects.equals(order.getCustomer().getId(), customerId.get())) {
			log.error("주문 상세 조회 - {} 가 소유한 주문이 아님", customerId.get());
			throw new OrderNotBelongsToUserException("해당 주문 정보의 소유자가 아닙니다.");
		}

		// 주문 요약 정보
		OrderSummaryDto orderSummaryDto = OrderSummaryDto.builder()
			.title(order.getTitle())
			.orderedAt(order.getOrderedAt())
			.build();

		// 배송 정보
		Shipping shipping = shippingRepository.findByOrder(order);

		ShippingInfoDto shippingInfoDto = ShippingInfoDto.builder()
			.recipientName(shipping.getRecipientName())
			.recipientPhoneNumber(shipping.getRecipientPhoneNumber())
			.recipientAddress(shipping.getRecipientAddress())
			.recipientAddressDetail(shipping.getRecipientAddressDetail())
			.recipientZipcode(shipping.getRecipientZipcode())
			.build();

		// 주문 항목
		OrderDetailItemPageDto orderDetailItem = orderDetailRepository.findOrderDetail(orderId, pageable);

		Page<OrderDetailItemDto> orderListResponses =
			new PageImpl<>(orderDetailItem.orderDetailItem(), pageable, orderDetailItem.totalCount());

		// 결제 항목
		PaymentInfoDto paymentInfoDto = orderRepository.findPaymentInfo(orderId);

		return OrderDetailResponse.builder()
			.order(orderSummaryDto)
			.orderItems(orderListResponses)
			.shipping(shippingInfoDto)
			.payment(paymentInfoDto).build();
	}

	/**
	 * 주문 취소 (결제 취소)
	 *
	 * @param customerId 사용자 아이디
	 * @param orderId 주문 아이디
	 * @return 응답 결과
	 */
	@Override
	@Transactional
	public ResponseMessage doOrderCancel(Long customerId, Long orderId, OrderCancelRequest orderCancelRequest) {

		// 회원이 맞는 지 확인
		Customer customer = getCustomerById(customerId);

		// 주문 정보 가져오기
		Order order = orderRepository.findByIdAndCustomer(orderId, customer).orElseThrow(
			() -> new OrderNotFoundException("해당 주문 정보가 존재하지 않습니다.")
		);

		// 결제 취소
		List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
		OrderState orderState = orderDetails.getFirst().getOrderState();
		ResponseMessage responseMessage = null;

		if (orderState.name().equals("PENDING")) {

			for (OrderDetail orderDetail : orderDetails) {
				orderDetail.setOrderState(OrderState.CANCELED);
				orderDetailRepository.save(orderDetail);
			}

			Optional<AllAppliedCoupon> allAppliedCoupon = allAppliedCouponRepository.findByOrder(order);
			if (allAppliedCoupon.isPresent()) {
				Optional<MemberCoupon> memberCoupon = memberCouponRepository.findById(
					allAppliedCoupon.get().getId());
				if (memberCoupon.isPresent()) {
					memberCoupon.get().setUsed(false);
					memberCouponRepository.save(memberCoupon.get());

					allAppliedCouponRepository.delete(allAppliedCoupon.get());
				}
			}

			Optional<OrderUsingPoint> orderUsingPoint = orderUsingPointRepository.findByOrder(order);

			if (orderUsingPoint.isPresent()) {

				Member member = memberRepository.findById(customerId).orElseThrow(
					() -> new MemberNotFoundException("회원 정보가 없습니다.")
				);

				PointHistory pointHistory = pointHistoryRepository.findById(orderUsingPoint.get().getId()).get();
				publisher.publishEvent(new OrderCancelPointEvent(member, order, pointHistory.getAmount().abs()));

			}

			return ResponseMessage.builder()
				.statusCode(200)
				.message("취소 완료")
				.build();

		} else if (orderState.name().equals("PAID")) {

			responseMessage = paymentService.cancelPayment(order, orderCancelRequest.reason());
			if (responseMessage.statusCode() == 200) {

				for (OrderDetail orderDetail : orderDetails) {
					orderDetail.setOrderState(OrderState.CANCELED);
					orderDetailRepository.save(orderDetail);
				}

				// 보상 처리
				Optional<AllAppliedCoupon> allAppliedCoupon = allAppliedCouponRepository.findByOrder(order);
				if (allAppliedCoupon.isPresent()) {
					Optional<MemberCoupon> memberCoupon = memberCouponRepository.findById(
						allAppliedCoupon.get().getId());
					memberCoupon.get().setUsed(false);
					memberCouponRepository.save(memberCoupon.get());

					allAppliedCouponRepository.delete(allAppliedCoupon.get());
				}

				Optional<OrderSavingPoint> orderSavingPoint = orderSavingPointRepository.findByOrder(order);
				Optional<OrderUsingPoint> orderUsingPoint = orderUsingPointRepository.findByOrder(order);

				if (orderUsingPoint.isPresent()) {

					Member member = memberRepository.findById(customerId).orElseThrow(
						() -> new MemberNotFoundException("회원 정보가 없습니다.")
					);

					PointHistory pointUsingHistory = pointHistoryRepository.findById(orderUsingPoint.get().getId())
						.get();
					PointHistory pointSavingHistory = pointHistoryRepository.findById(orderSavingPoint.get().getId())
						.get();

					BigDecimal result = pointUsingHistory.getAmount().abs().subtract(pointSavingHistory.getAmount());

					publisher.publishEvent(new OrderCancelPointEvent(member, order, result));

				}

			}

			return responseMessage;

		} else {
			return ResponseMessage.builder()
				.statusCode(401)
				.message("취소할 수 없는 주문입니다.")
				.build();
		}

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
	private Order createOrder(Customer customer, OrderRegisterRequest orderTempRegisterRequest) {

		BigDecimal wrappingCost = BigDecimal.ZERO;
		WrappingPaper wrappingPaper = null;
		if (Objects.nonNull(orderTempRegisterRequest.wrapping())) {
			wrappingPaper = wrappingPaperService.getWrappingPaper(
				orderTempRegisterRequest.wrapping());
			wrappingCost = wrappingPaper.getWrappingPrice();
		}

		return Order.builder()
			.customer(customer)
			.paymentPrice(orderTempRegisterRequest.paymentPrice())
			.orderedAt(LocalDateTime.now())
			.wrappingPrice(wrappingCost)
			.expectedDeliveryAt(orderTempRegisterRequest.expectedDeliveryAt())
			.wrappingPaper(wrappingPaper)
			.booksPrice(orderTempRegisterRequest.paymentBooks())
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

		member.ifPresent(value ->
			publisher.publishEvent(new PointUsedEvent(value, savedOrder, usedPoint))
		);
	}

	private void processCoupon(OrderRegisterRequest orderTempRegisterRequest, Order savedOrder) {
		MemberCoupon memberCoupon = memberCouponRepository.findById(orderTempRegisterRequest.allAppliedCoupon())
			.orElseThrow(MemberCartNotFoundException::new);

		BigDecimal discountPrice = BigDecimal.valueOf(memberCoupon.getCoupon().getDiscount());

		// 쿠폰 정책에 따라 할인가 적용
		if (memberCoupon.getCoupon().getPolicyType().compareTo(PolicyType.RATED) == 0) {
			BigDecimal tempPrice = orderTempRegisterRequest.paymentBooks()
				.multiply(discountPrice.divide(BigDecimal.valueOf(100), 1, RoundingMode.HALF_UP));

			if (tempPrice.compareTo(memberCoupon.getCoupon().getMaximumDiscountPrice()) >= 0) {
				discountPrice = memberCoupon.getCoupon().getMaximumDiscountPrice();
			}
		}

		// AllAppliedCoupon 생성 및 저장
		AllAppliedCoupon allAppliedCoupon = AllAppliedCoupon.builder()
			.memberCoupon(memberCoupon)
			.order(savedOrder)
			.discount_price(discountPrice)
			.build();

		allAppliedCouponRepository.save(allAppliedCoupon);

		// 쿠폰 사용 처리
		memberCoupon.setUsed(true);
		memberCouponRepository.save(memberCoupon);
	}

	private List<OrderDetailRequest> processWrapping(List<OrderDetailRequest> orderDetails, String wrappingList) {
		// 포장할 책 ID 목록을 가져와서 Set으로 변환
		String[] bookIds = wrappingList.split(",");
		Set<Long> wrappingBookIds = Arrays.stream(bookIds)
			.map(Long::parseLong)
			.collect(Collectors.toSet());

		// orderDetails를 순회하며 wrapping을 적용
		List<OrderDetailRequest> updatedOrderDetails = new ArrayList<>();
		for (OrderDetailRequest orderDetailRequest : orderDetails) {
			Long bookId = orderDetailRequest.bookId();

			// 책 ID가 wrappingBookIds에 포함되어 있으면 wrapping을 true로 설정
			if (wrappingBookIds.contains(bookId)) {
				OrderDetailRequest updatedOrderDetail = new OrderDetailRequest(
					bookId, orderDetailRequest.count(), orderDetailRequest.unitPrice(), true, null);
				updatedOrderDetails.add(updatedOrderDetail);
			} else {
				updatedOrderDetails.add(orderDetailRequest);
			}
		}
		return updatedOrderDetails;
	}

}
