package shop.nuribooks.books.order.order.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;
import shop.nuribooks.books.book.bookcontributor.repository.BookContributorRepository;
import shop.nuribooks.books.book.coupon.dto.MemberCouponOrderDto;
import shop.nuribooks.books.book.coupon.service.MemberCouponService;
import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.member.address.entity.Address;
import shop.nuribooks.books.member.address.repository.AddressRepository;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.customer.repository.CustomerRepository;
import shop.nuribooks.books.member.member.dto.MemberPointDTO;
import shop.nuribooks.books.member.member.repository.MemberRepository;
import shop.nuribooks.books.order.order.dto.response.OrderInformationResponse;
import shop.nuribooks.books.order.shipping.repository.ShippingPolicyRepository;
import shop.nuribooks.books.order.wrapping.dto.WrappingPaperResponse;
import shop.nuribooks.books.order.wrapping.service.WrappingPaperService;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

	@Mock
	private BookContributorRepository bookContributorRepository;
	@Mock
	private WrappingPaperService wrappingPaperService;
	@Mock
	private BookRepository bookRepository;
	@Mock
	private ShippingPolicyRepository shippingPolicyRepository;
	@Mock
	private CustomerRepository customerRepository;
	@Mock
	private AddressRepository addressRepository;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private MemberCouponService memberCouponService;
	@InjectMocks
	private OrderServiceImpl orderService;

	private Book book;
	private Customer customer;
	private BookContributorInfoResponse contributor;

	@BeforeEach
	void setUp() {
		book = TestUtils.createBook(TestUtils.createPublisher());
		TestUtils.setIdForEntity(book, 1L);

		customer = TestUtils.createCustomer();
		TestUtils.setIdForEntity(customer, 1L);

		contributor = new BookContributorInfoResponse(
			1L, "정누리", 1L, "작가"
		);
	}

	@Test
	@DisplayName("주문 폼 정보 불러오기 - 비회원")
	void getCustomerOrderInformation() {

		// given
		Long bookId = 1L;
		int quantity = 2;
		List<BookContributorInfoResponse> contributors = List.of(contributor);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(bookContributorRepository.findContributorsAndRolesByBookId(bookId)).thenReturn(contributors);

		when(shippingPolicyRepository.findClosedShippingPolicy(anyInt())).thenReturn(
			TestUtils.createShippingPolicy());

		List<WrappingPaperResponse> wrappingPapers = List.of(mock(WrappingPaperResponse.class));
		when(wrappingPaperService.getAllWrappingPaper()).thenReturn(wrappingPapers);

		// when
		OrderInformationResponse response = orderService.getCustomerOrderInformation(bookId, quantity);

		// then
		assertNotNull(response);
		assertEquals(1, response.bookOrderResponse().size());  // 1개의 책 정보가 있어야 함

		// verify
		verify(wrappingPaperService, times(1)).getAllWrappingPaper();
	}

	@Test
	@DisplayName("주문 폼 정보 불러오기 - 회원")
	void getMemberOrderInformation() {

		// given
		Long memberId = 1L;
		Long bookId = 1L;
		int quantity = 2;
		List<BookContributorInfoResponse> contributors = List.of(contributor);

		when(customerRepository.findById(memberId)).thenReturn(Optional.of(customer));

		List<Address> addresses = List.of(mock(Address.class));
		when(addressRepository.findAllByMemberId(memberId)).thenReturn(addresses);

		Optional<MemberPointDTO> point = Optional.of(new MemberPointDTO(new BigDecimal(1000)));
		when(memberRepository.findPointById(memberId)).thenReturn(point);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(bookContributorRepository.findContributorsAndRolesByBookId(bookId)).thenReturn(contributors);

		when(shippingPolicyRepository.findClosedShippingPolicy(anyInt())).thenReturn(
			TestUtils.createShippingPolicy());

		List<WrappingPaperResponse> wrappingPapers = List.of(mock(WrappingPaperResponse.class));
		when(wrappingPaperService.getAllWrappingPaper()).thenReturn(wrappingPapers);

		List<MemberCouponOrderDto> memberCouponOrders = List.of(mock(MemberCouponOrderDto.class));
		when(memberCouponService.getCouponsApplicableToOrder(anyLong(), anyList())).thenReturn(memberCouponOrders);

		// when
		OrderInformationResponse response = orderService.getMemberOrderInformation(memberId, bookId, quantity);

		// then
		assertNotNull(response);
		assertEquals(1, response.bookOrderResponse().size());  // 1개의 책 정보가 있어야 함

		// verify
		verify(wrappingPaperService, times(1)).getAllWrappingPaper();
	}

}