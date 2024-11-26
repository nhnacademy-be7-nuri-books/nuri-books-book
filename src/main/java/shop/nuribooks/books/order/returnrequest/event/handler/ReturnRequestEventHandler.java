// package shop.nuribooks.books.order.returnrequest.event.handler;
//
// import java.math.BigDecimal;
//
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.event.TransactionPhase;
// import org.springframework.transaction.event.TransactionalEventListener;
//
// import lombok.RequiredArgsConstructor;
// import shop.nuribooks.books.book.point.enums.PolicyName;
// import shop.nuribooks.books.book.point.service.PointHistoryService;
// import shop.nuribooks.books.member.member.entity.Member;
// import shop.nuribooks.books.order.order.entity.Order;
// import shop.nuribooks.books.order.returnrequest.event.ReturnRequestEvent;
//
// @Component
// @RequiredArgsConstructor
// public class ReturnRequestEventHandler {
// 	private final PointHistoryService pointHistoryService;
//
// 	// point event listener 추가.
// 	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
// 	public void createPointHistory(ReturnRequestEvent event) {
// 		Member member = event.getMember();
// 		Order order = event.getOrder();
// 		BigDecimal amount = event.getAmount();
// 		RefundReturningPoint refundReturningPoint = new RefundReturningPoint(member, order, amount);
// 		pointHistoryService.registerPointHistory(refundReturningPoint, PolicyName.USING);
// 	}
// }
