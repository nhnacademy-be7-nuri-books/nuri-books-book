// package shop.nuribooks.books.book.point.dto.request.register;
//
// import jakarta.validation.constraints.NotNull;
// import shop.nuribooks.books.book.point.entity.PointHistory;
// import shop.nuribooks.books.book.point.entity.PointPolicy;
// import shop.nuribooks.books.book.point.entity.child.RefundReturningPoint;
// import shop.nuribooks.books.member.member.entity.Member;
//
// public class RefundReturningPointRequest extends PointHistoryRequest {
// 	@NotNull
// 	Refund refund;
// 	PointHistory savedPointHistory;
//
// 	public RefundReturningPointRequest(Member member, Refund refund, PointHistory savedPointHistory) {
// 		super(member);
// 		this.refund = refund;
// 		// 이전 주문에서 적립된 금액만큼 빼줘야함. 환불에서 취소된 금액도 찾아와야함.
// 		this.savedPointHistory = savedPointHistory;
// 	}
//
// 	public RefundReturningPoint toEntity(PointPolicy pointPolicy) {
// 		return RefundReturningPoint.builder()
// 			.amount(refund.amount().minus(savedPointHistory.getAmount()))
// 			.description("환불 반환 포인트. (사용한 포인트 포함, 적립된 포인트 제외)")
// 			.member(member)
// 			.pointPolicy(pointPolicy)
// 			.refund(refund)
// 			.build();
// 	}
// }
