package shop.nuribooks.books.order.order.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import shop.nuribooks.books.common.TestUtils;
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
	private CommonOrderService commonOrderService;
	@InjectMocks
	private OrderServiceImpl orderService;

	private Book book;
	private BookContributorInfoResponse contributor;

	@BeforeEach
	void setUp() {
		book = TestUtils.createBook(TestUtils.createPublisher());
		TestUtils.setIdForEntity(book, 1L);

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

}