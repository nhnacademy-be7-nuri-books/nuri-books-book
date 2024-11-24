package shop.nuribooks.books.order.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.AllArgsConstructor;
import shop.nuribooks.books.book.book.dto.BookOrderResponse;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;
import shop.nuribooks.books.book.bookcontributor.repository.BookContributorRepository;
import shop.nuribooks.books.cart.repository.RedisCartRepository;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.member.address.dto.response.AddressResponse;
import shop.nuribooks.books.member.address.entity.Address;
import shop.nuribooks.books.member.address.repository.AddressRepository;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.customer.repository.CustomerRepository;
import shop.nuribooks.books.member.member.dto.MemberPointDTO;
import shop.nuribooks.books.member.member.repository.MemberRepository;
import shop.nuribooks.books.order.shipping.entity.ShippingPolicy;
import shop.nuribooks.books.order.shipping.repository.ShippingPolicyRepository;
import shop.nuribooks.books.order.shipping.service.ShippingService;

@AllArgsConstructor
public abstract class AbstractOrderService {

	protected final CustomerRepository customerRepository;
	protected final BookRepository bookRepository;
	protected final AddressRepository addressRepository;
	protected final BookContributorRepository bookContributorRepository;
	protected final RedisCartRepository redisCartRepository;
	protected final ShippingPolicyRepository shippingPolicyRepository;
	protected final MemberRepository memberRepository;

	protected final ShippingService shippingService;

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
	 * 	배송비 정책 조회
	 */
	protected ShippingPolicy getShippingPolicy(int orderTotalPrice) {
		return shippingPolicyRepository.findClosedShippingPolicy(orderTotalPrice);
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
}
