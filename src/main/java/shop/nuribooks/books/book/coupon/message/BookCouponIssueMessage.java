package shop.nuribooks.books.book.coupon.message;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookCouponIssueMessage {
	private Long couponId;
	private Long memberId;
	private String messageId;

	@Builder
	public BookCouponIssueMessage(Long couponId, Long memberId, String messageId) {
		this.couponId = couponId;
		this.memberId = memberId;
		this.messageId = messageId;
	}
}
