package shop.nuribooks.books.order.order.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.customer.repository.CustomerRepository;
import shop.nuribooks.books.order.order.dto.OrderTempRegisterRequest;
import shop.nuribooks.books.order.order.dto.OrderTempRegisterResponse;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.order.repository.OrderRepository;
import shop.nuribooks.books.order.orderDetail.dto.OrderDetailRequest;
import shop.nuribooks.books.order.orderDetail.service.OrderDetailService;
import shop.nuribooks.books.order.shipping.service.ShippingService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderServiceImpl implements OrderService{

	private final CustomerRepository customerRepository;
	private final OrderRepository orderRepository;

	private final OrderDetailService orderDetailService;
	private final ShippingService shippingService;

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
		Customer customer = customerRepository.findById(id)
			.orElseThrow(() -> new MemberNotFoundException("등록되지 않은 사용자입니다."));

		// todo : 회원 쿠폰 처리
		// todo : 회원 포인트 차감

		// 임시 주문 등록
		Order order = Order.builder()
			.customer(customer)
			.paymentPrice(orderTempRegisterRequest.paymentPrice())
			.orderedAt(orderTempRegisterRequest.orderedAt())
			.wrappingPrice(orderTempRegisterRequest.wrappingPrice())
			.build();

		orderRepository.save(order);

		// 주문 상세 등록
		List<String> bookTitles = new ArrayList<>();
		for(OrderDetailRequest detail : orderTempRegisterRequest.orderDetails() ){
			orderDetailService.registerOrderDetail(order, detail);

			// 책 제목 추출
			bookTitles.add(orderDetailService.getBookTitle(detail.bookId()));

		}

		// 배송지 등록
		shippingService.registerShipping(order, orderTempRegisterRequest.shippingRegister());

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
		// todo :  사용자 생성
		Customer customer = null;


		// 임시 주문 등록
		Order order = Order.builder()
			.customer(customer)
			.paymentPrice(orderTempRegisterRequest.paymentPrice())
			.orderedAt(orderTempRegisterRequest.orderedAt())
			.wrappingPrice(orderTempRegisterRequest.wrappingPrice())
			.build();

		orderRepository.save(order);

		// 주문 상세 등록
		List<String> bookTitles = new ArrayList<>();
		for(OrderDetailRequest detail : orderTempRegisterRequest.orderDetails() ){
			orderDetailService.registerOrderDetail(order, detail);

			// 책 제목 추출
			bookTitles.add(orderDetailService.getBookTitle(detail.bookId()));

		}

		// 배송지 등록
		shippingService.registerShipping(order, orderTempRegisterRequest.shippingRegister());

		// 응답 반환
		return OrderTempRegisterResponse.of(order, makeOrderName(bookTitles));
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
	private String makeOrderName(List<String> bookTitles){

		if (Objects.isNull(bookTitles)) {
			return "";
		}

		if(bookTitles.size() == 1){
			return bookTitles.getFirst() + " 1건";
		}else{
			String firstBook = bookTitles.getFirst();
			int bookListCount = bookTitles.size() - 1;
			return firstBook + " 외 " + bookListCount + "건";
		}
	}
}
