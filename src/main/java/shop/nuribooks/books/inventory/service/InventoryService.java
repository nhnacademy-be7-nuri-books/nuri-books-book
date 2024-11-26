package shop.nuribooks.books.inventory.service;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.exception.book.BookIdNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

	private final BookRepository bookRepository;

	@Transactional
	public void updateStock(Long bookId, int count) {
		Book book = bookRepository.findByIdAndDeletedAtIsNull(bookId)
			.orElseThrow(BookIdNotFoundException::new);

		//재고 차감 및 상태 변경
		try {
			book.updateStock(count);
			//영속성 컨텍스트로 관리되므로 bookRepository.save 호출 불필요
			log.info("Stock updated for bookId : {}: -{}", bookId, count);
		} catch (Exception e) {
			log.error("Failed to update stock for bookId {}: {}", bookId, e.getMessage());
			throw new AmqpRejectAndDontRequeueException("재고가 없습니다." + bookId);
		}
	}
}
