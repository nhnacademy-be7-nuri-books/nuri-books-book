package shop.nuribooks.books.book.point.event.handler;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.point.event.PointHistoryEvent;
import shop.nuribooks.books.book.point.service.PointHistoryService;

@Component
@RequiredArgsConstructor
public class PointHistoryEventHandler {
	private final PointHistoryService pointHistoryService;

	@EventListener
	public void createPointHistory(PointHistoryEvent event) {
		pointHistoryService.registerPointHistory(event.getPointHistoryRequest(), event.getPolicyName());
	}
}
