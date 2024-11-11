// package shop.nuribooks.books.book.point.dto.request.register;
//
// import java.math.BigDecimal;
//
// import jakarta.validation.constraints.NotNull;
// import shop.nuribooks.books.book.point.entity.PointPolicy;
// import shop.nuribooks.books.book.point.entity.child.RefundReturningPoint;
// import shop.nuribooks.books.member.member.entity.Member;
//
// public class RefundReturningPointRequest extends PointHistoryRequest {
// 	@NotNull
// 	Refund refund;
// 	BigDecimal amount;
//
// 	public RefundReturningPointRequest(Member member, Refund refund, BigDecimal amount) {
// 		super(member);
// 		this.refund = refund;
// 		// 이전 주문에서 적립된 금액만큼 빼줘야함. 환불에서 취소된 금액도 찾아와야함.
// 		this.amount = amount;
// 	}
//
// 	public RefundReturningPoint toEntity(PointPolicy pointPolicy) {
// 		return RefundReturningPoint.builder()
// 			.amount(amount)
// 			.description("환불 반환 포인트. (사용한 포인트 포함, 적립된 포인트 제외)")
// 			.member(member)
// 			.pointPolicy(pointPolicy)
// 			.refund(refund)
// 			.build();
// 	}
// }
