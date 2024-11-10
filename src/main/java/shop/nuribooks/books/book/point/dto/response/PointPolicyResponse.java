package shop.nuribooks.books.book.point.dto.response;

import java.math.BigDecimal;

import shop.nuribooks.books.book.point.enums.PolicyType;

public interface PointPolicyResponse {
	long getId();

	PolicyType getPolicyType();

	String getName();

	BigDecimal getAmount();
}
