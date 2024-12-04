package shop.nuribooks.books.order.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import lombok.AllArgsConstructor;
import shop.nuribooks.books.book.book.dto.BookOrderResponse;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;
import shop.nuribooks.books.book.bookcontributor.repository.BookContributorRepository;
import shop.nuribooks.books.book.coupon.dto.MemberCouponOrderDto;
import shop.nuribooks.books.book.coupon.enums.DiscountType;
import shop.nuribooks.books.book.coupon.service.MemberCouponService;
import shop.nuribooks.books.cart.repository.RedisCartRepository;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.exception.order.OrderNotFoundException;
import shop.nuribooks.books.member.address.dto.response.AddressResponse;
import shop.nuribooks.books.member.address.entity.Address;
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

@AllArgsConstructor
public class CommonOrderService {

	protected final CustomerRepository customerRepository;
	protected final BookRepository bookRepository;
	protected final AddressRepository addressRepository;
	protected final BookContributorRepository bookContributorRepository;
	protected final RedisCartRepository redisCartRepository;
	protected final ShippingPolicyRepository shippingPolicyRepository;
	protected final MemberRepository memberRepository;
	protected final OrderRepository orderRepository;

	protected final ShippingService shippingService;
	protected final WrappingPaperService wrappingPaperService;
	protected final MemberCouponService memberCouponService;

	/**
	 * 주문 가격 검증
	 * @param orderTempRegisterRequest 클라이언트에서 받은 주문 정보
	 * @return 검증 여부
	 */
	public boolean validateOrderPrice(OrderRegisterRequest orderTempRegisterRequest) {

		// 클라이언트에서 받은 총 결제 금액
		BigDecimal orderTotalPrice = orderTempRegisterRequest.paymentPrice();

		// 도서 총 가격
		List<OrderDetailRequest> orderDetails = orderTempRegisterRequest.orderDetails();
		BigDecimal bookTotalPrice = orderDetails.stream()
			.map(orderDetail -> orderDetail.unitPrice().multiply(BigDecimal.valueOf(orderDetail.count())))
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		// 배송비 (배송 정책에 따른 계산)
		ShippingPolicyResponse shippingPolicy = getShippingPolicy(bookTotalPrice.intValue());
		BigDecimal shippingCost = BigDecimal.valueOf(shippingPolicy.shippingFee());

		// 사용 포인트
		BigDecimal usedPoint = orderTempRegisterRequest.usingPoint();

		WrappingPaper wrappingPaper = null;
		// 포장비
		if (Objects.nonNull(orderTempRegisterRequest.wrapping())) {
			wrappingPaper = wrappingPaperService.getWrappingPaper(
				orderTempRegisterRequest.wrapping());
		}

		BigDecimal wrappingCost = wrappingPaper == null ? BigDecimal.ZERO : wrappingPaper.getWrappingPrice();

		MemberCouponOrderDto memberCouponAllType = null;
		if (Objects.nonNull(orderTempRegisterRequest.allAppliedCoupon())) {
			memberCouponAllType = memberCouponService.getMemberCoupon(
				orderTempRegisterRequest.allAppliedCoupon());
		}

		// 쿠폰 할인 적용된 금액 (쿠폰 적용 후 금액)
		BigDecimal couponDiscount = memberCouponAllType == null ? BigDecimal.ZERO :
			BigDecimal.valueOf(memberCouponAllType.discount());  // 쿠폰 할인액

		BigDecimal calculatedTotalPrice;

		if (Objects.isNull(memberCouponAllType)
			|| memberCouponAllType.discountType().compareTo(DiscountType.FIXED) == 0) {
			calculatedTotalPrice = bookTotalPrice
				.subtract(couponDiscount) // 쿠폰 값 제외
				.subtract(usedPoint) // 사용된 포인트 제외
				.add(wrappingCost) // 포장비 추가
				.add(shippingCost); // 배송비 추가
		} else {
			BigDecimal discountPrice = bookTotalPrice.multiply(couponDiscount.divide(BigDecimal.valueOf(100)));
			if (discountPrice.compareTo(memberCouponAllType.maximumDiscountPrice()) >= 0) {
				calculatedTotalPrice = bookTotalPrice
					.subtract(memberCouponAllType.maximumDiscountPrice()) // 쿠폰 값 제외
					.subtract(usedPoint) // 사용된 포인트 제외
					.add(wrappingCost) // 포장비 추가
					.add(shippingCost); // 배송비 추가
			} else {
				calculatedTotalPrice = bookTotalPrice
					.subtract(discountPrice) // 쿠폰 값 제외
					.subtract(usedPoint) // 사용된 포인트 제외
					.add(wrappingCost) // 포장비 추가
					.add(shippingCost); // 배송비 추가
			}
		}

		return calculatedTotalPrice.compareTo(orderTotalPrice) == 0;
	}

	/**
	 * 주문 정보 찾기
	 *
	 * @param orderId 주문 아이디
	 * @return 주문 정보
	 */
	protected Order getOrderById(Long orderId) {
		return orderRepository.findById(orderId).orElseThrow(
			() -> new OrderNotFoundException("해당 주문 정보가 존재하지 않습니다.")
		);
	}

	/**
	 * 사용자 확인
	 *
	 * @param id 사용자 아이디
	 * @return Customer
	 */
	protected Customer getCustomerById(Long id) {
		return customerRepository.findById(id)
			.orElseThrow(() -> new MemberNotFoundException("등록되지 않은 사용자입니다."));
	}

	/**
	 * 사용자로 부터 주소 목록 가져오기
	 *
	 * @param customer 사용자
	 * @return 주소 목록
	 */
	protected List<AddressResponse> getAddressesByMember(Customer customer) {
		List<Address> addressesByMemberId = addressRepository.findAllByMemberId(customer.getId());
		return addressesByMemberId.stream().map(AddressResponse::of).toList();
	}

	/**
	 * 사용자 정보 DTO 생성
	 *
	 * @param customer 사용자
	 * @param addressResponseList 사용자 주소
	 * @param point 사용자 포인트
	 * @return CustomerDto
	 */
	protected CustomerDto createCustomerDto(Customer customer, List<AddressResponse> addressResponseList,
		Optional<MemberPointDTO> point) {
		return new CustomerDto(
			customer.getId(),
			customer.getName(),
			customer.getPhoneNumber(),
			customer.getEmail(),
			point.get().point(),
			addressResponseList
		);
	}

	/**
	 * 도서 정보 가져오기
	 *
	 * @param bookId 책 아이디
	 * @param quantity 수량
	 * @return BookOrderResponse
	 */
	protected BookOrderResponse getBookOrderResponses(Long bookId, int quantity) {
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
	protected BigDecimal calculateTotalPrice(List<BookOrderResponse> bookOrderResponses) {
		BigDecimal totalPrice = BigDecimal.ZERO;
		for (BookOrderResponse bookOrder : bookOrderResponses) {
			totalPrice = totalPrice.add(bookOrder.bookTotalPrice());
		}
		return totalPrice;
	}

	/**
	 * 	주문 가격에 알맞는 배송비 가져오기
	 */
	protected ShippingPolicyResponse getShippingPolicy(int orderTotalPrice) {
		ShippingPolicy shippingPolicy = shippingPolicyRepository.findClosedShippingPolicy(orderTotalPrice);

		return new ShippingPolicyResponse(
			shippingPolicy.getId(),
			shippingPolicy.getShippingFee(),
			shippingPolicy.getExpiration(),
			shippingPolicy.getMinimumOrderPrice()
		);
	}

	/**
	 * 포인트 가져오기
	 * @param id 사용자 아이디
	 * @return 포인트
	 */
	protected Optional<MemberPointDTO> getMemberPoints(Long id) {
		return memberRepository.findPointById(id);
	}

	/**
	 * 장바구니를 통한 책 정보 가져오기
	 *
	 * @param cart 장바구니
	 * @return 책 목록
	 */
	protected List<BookOrderResponse> getBookOrderResponsesFromCart(Map<Long, Integer> cart) {
		return new ArrayList<>(cart.entrySet().stream()
			.map(entry -> getBookOrderResponses(entry.getKey(), entry.getValue()))
			.toList());
	}

	/**
	 * 주문 정보 가져오기
	 * @param orderId 주문아이디
	 * @return 주문 정보
	 */
	protected Order getOrder(Long orderId) {
		return orderRepository.findById(orderId).orElseThrow(
			() -> new OrderNotFoundException("해당 주문 정보가 존재하지 않습니다.")
		);
	}
}
