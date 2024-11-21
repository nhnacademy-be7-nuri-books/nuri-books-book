package shop.nuribooks.books.order.orderDetail.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.order.NoStockAvailableException;
import shop.nuribooks.books.exception.order.OrderDetailNotFoundException;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.orderDetail.dto.OrderDetailRequest;
import shop.nuribooks.books.order.orderDetail.entity.OrderDetail;
import shop.nuribooks.books.order.orderDetail.entity.OrderState;
import shop.nuribooks.books.order.orderDetail.repository.OrderDetailRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDetailServiceImpl implements OrderDetailService {

	private final BookRepository bookRepository;
	private final OrderDetailRepository orderDetailRepository;

	@Override
	public void registerOrderDetail(Order order, OrderDetailRequest orderDetailRequest) {

		// 상품 확인
		Book book = bookRepository.findById(orderDetailRequest.bookId())
			.orElseThrow(() -> new BookNotFoundException(orderDetailRequest.bookId()));

		// todo : 재고 확인 (동시성 고려) - RabbitMQ
		if (orderDetailRequest.count() > book.getStock()) {
			// 예외 처리
			throw new NoStockAvailableException();
		}

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

	/**
	 * 주문 상세 중 하나라도 재고가 없는 지 확인
	 *
	 * @param order 주문 정보
	 * @return 재고 여부
	 */
	@Override
	public boolean checkStock(Order order) {

		List<OrderDetail> orderDetailList = orderDetailRepository.findAllByOrderId(order.getId());

		boolean availableStock = true;

		for (OrderDetail orderDetail : orderDetailList) {
			Book book = orderDetail.getBook();

			// 재고 확인
			if (orderDetail.getCount() > book.getStock()) {
				availableStock = false;
			}
		}

		return availableStock;
	}

	@Override
	public void refundUpdateStateAndStock(OrderDetail orderDetail) {
		orderDetail.setOrderState(OrderState.RETURNED);
		int increaseStock = -orderDetail.getCount();
		orderDetail.getBook().updateStock(increaseStock);
	}

	@Override
	public OrderDetail getOrderDetail(Long orderDetailId) {
		return orderDetailRepository.findById(orderDetailId)
			.orElseThrow(() -> new OrderDetailNotFoundException("주문 상세 정보를 찾을 수 없습니다."));
	}

}
