package shop.nuribooks.books.order.orderDetail.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.orderDetail.dto.OrderDetailRequest;
import shop.nuribooks.books.order.orderDetail.entity.OrderDetail;
import shop.nuribooks.books.order.orderDetail.entity.OrderState;
import shop.nuribooks.books.order.orderDetail.repository.OrderDetailRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDetailServiceImpl implements OrderDetailService{

	private final BookRepository bookRepository;
	private final OrderDetailRepository orderDetailRepository;

	@Override
	public void registerOrderDetail(Order order, OrderDetailRequest orderDetailRequest) {

		// 상품 확인
		Book book = bookRepository.findById(orderDetailRequest.bookId())
			.orElseThrow(() -> new BookNotFoundException(orderDetailRequest.bookId()));

		// todo : 재고 확인 (동시성 고려) - RabbitMQ
		if(orderDetailRequest.count() > book.getStock()){
			// 예외 처리

		}

		// todo : 재고 차감은 최종 결제 시에 재고 확인하고 처리

		// 주문 상세 추가
		OrderDetail orderDetail = OrderDetail.builder()
			.order(order)
			.orderState(OrderState.PENDING)
			.book(book)
			.count(orderDetailRequest.count())
			.unitPrice(orderDetailRequest.unitPrice())
			.isWrapped(orderDetailRequest.isWrapped())
			.build();

		orderDetailRepository.save(orderDetail);
	}

	@Override
	public String getBookTitle(Long bookId) {
		return bookRepository.findById(bookId)
			.map(Book::getTitle).orElse("정보 없음");
	}
}
