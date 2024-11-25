package shop.nuribooks.books.book.point.event;

import lombok.AllArgsConstructor;
import shop.nuribooks.books.book.point.dto.request.register.PointHistoryRequest;
import shop.nuribooks.books.book.point.enums.PolicyName;

@AllArgsConstructor
public class PointHistoryEvent {
	private PointHistoryRequest pointHistoryRequest;
	private PolicyName policyName;

	public PointHistoryRequest getPointHistoryRequest() {
		return this.pointHistoryRequest;
	}

	public PolicyName getPolicyName() {
		return this.policyName;
	}
}
